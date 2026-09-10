/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2026 DeDiamondPro
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package dev.dediamondpro.buildsource

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import java.io.File
import java.nio.file.Files
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Fixes compatibility with VulkanMod.
 *
 * Resourcify renders its whole GUI into an offscreen texture and then blits it back with a
 * vertical flip (sampled V 1 -> 0) to compensate for the mirrored texture layout on the OpenGL
 * backend. VulkanMod renders that texture already upright, so the compensating flip ends up
 * being applied a second time and the whole UI is drawn upside down. This task rewrites the
 * shaded `AdvancedDrawContext.draw` so the flip is only applied when VulkanMod is NOT loaded,
 * fixing the rendering on Vulkan while keeping the OpenGL behavior untouched.
 */
abstract class VulkanCompatPatchTask : DefaultTask() {

    @get:InputFile
    abstract val inputJar: RegularFileProperty

    @get:Input
    abstract val patchEnabled: Property<Boolean>

    @get:Input
    abstract val failOnMiss: Property<Boolean>

    @TaskAction
    fun patch() {
        if (!patchEnabled.get()) return
        val jar = inputJar.get().asFile
        val tmp = File.createTempFile("resourcify-patch", ".jar")
        try {
            var patchedEntries = 0
            ZipInputStream(Files.newInputStream(jar.toPath())).use { zis ->
                ZipOutputStream(Files.newOutputStream(tmp.toPath())).use { zos ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        val content = zis.readAllBytes()
                        var outContent = content
                        if (entry.name.endsWith("AdvancedDrawContext.class")) {
                            val patched = patchClass(content)
                            if (patched != null) {
                                outContent = patched
                                patchedEntries++
                                logger.lifecycle("VulkanMod compat: patched {}", entry.name)
                            }
                        }
                        val outEntry = java.util.zip.ZipEntry(entry.name)
                        zos.putNextEntry(outEntry)
                        zos.write(outContent)
                        zos.closeEntry()
                        entry = zis.nextEntry
                    }
                }
            }
            if (patchedEntries == 0 && failOnMiss.get()) {
                throw GradleException(
                    "VulkanMod compat patch found no AdvancedDrawContext.draw to patch. " +
                        "The bundled universalcraft layout probably changed; update " +
                        "dev/dediamondpro/buildsource/VulkanCompatPatchTask."
                )
            }
            Files.move(tmp.toPath(), jar.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        } finally {
            tmp.delete()
        }
    }

    /**
     * Returns patched class bytes, or null if this class does not need patching.
     */
    private fun patchClass(classBytes: ByteArray): ByteArray? {
        val cn = ClassNode()
        ClassReader(classBytes).accept(cn, 0)

        val draw = cn.methods.firstOrNull { it.name == "draw" } ?: return null

        // Idempotency: already patched classes reference FabricLoader.
        val alreadyPatched = draw.instructions
            .any {
                it is MethodInsnNode &&
                    it.owner == "net/fabricmc/loader/api/FabricLoader"
            }
        if (alreadyPatched) return null

        val flag = draw.maxLocals
        draw.maxLocals = draw.maxLocals + 1

        val head = InsnList()
        head.add(
            MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "net/fabricmc/loader/api/FabricLoader",
                "getInstance",
                "()Lnet/fabricmc/loader/api/FabricLoader;",
                true
            )
        )
        head.add(LdcInsnNode("vulkanmod"))
        head.add(
            MethodInsnNode(
                Opcodes.INVOKEINTERFACE,
                "net/fabricmc/loader/api/FabricLoader",
                "isModLoaded",
                "(Ljava/lang/String;)Z",
                true
            )
        )
        head.add(VarInsnNode(Opcodes.ISTORE, flag))
        draw.instructions.insert(head)

        val nodes: List<AbstractInsnNode> = draw.instructions.toArray().toList()
        var patchedA = 0
        var patchedB = 0
        for (idx in 0 until nodes.size - 1) {
            val a: AbstractInsnNode = nodes[idx]
            val b: AbstractInsnNode = nodes[idx + 1]
            if (!(a is VarInsnNode && a.opcode == Opcodes.ILOAD && a.`var` == 4)) {
                continue
            }
            when (b.opcode) {
                Opcodes.I2F -> {
                    // (float)height pushed as the blit 'v' coordinate
                    val repl = InsnList()
                    val skip = LabelNode()
                    val end = LabelNode()
                    repl.add(VarInsnNode(Opcodes.ILOAD, flag))
                    repl.add(JumpInsnNode(Opcodes.IFNE, skip))
                    repl.add(VarInsnNode(Opcodes.ILOAD, 4))
                    repl.add(InsnNode(Opcodes.I2F))
                    repl.add(JumpInsnNode(Opcodes.GOTO, end))
                    repl.add(skip)
                    repl.add(InsnNode(Opcodes.FCONST_0))
                    repl.add(end)
                    draw.instructions.insert(a, repl)
                    draw.instructions.remove(a)
                    draw.instructions.remove(b)
                    patchedA++
                }
                Opcodes.INEG -> {
                    // -height vertical flip compensation
                    val repl = InsnList()
                    val skip = LabelNode()
                    val end = LabelNode()
                    repl.add(VarInsnNode(Opcodes.ILOAD, flag))
                    repl.add(JumpInsnNode(Opcodes.IFNE, skip))
                    repl.add(VarInsnNode(Opcodes.ILOAD, 4))
                    repl.add(InsnNode(Opcodes.INEG))
                    repl.add(JumpInsnNode(Opcodes.GOTO, end))
                    repl.add(skip)
                    repl.add(VarInsnNode(Opcodes.ILOAD, 4))
                    repl.add(end)
                    draw.instructions.insert(a, repl)
                    draw.instructions.remove(a)
                    draw.instructions.remove(b)
                    patchedB++
                }
            }
        }
        if (patchedA != 1 || patchedB != 1) {
            throw GradleException(
                "VulkanMod compat patch failed on ${cn.name}: " +
                    "expected 1 (float)height push and 1 height negation in draw(), " +
                    "found $patchedA and $patchedB."
            )
        }

        val cw = object : ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS) {
            override fun getCommonSuperClass(a: String, b: String): String {
                if (a == b || a == "java/lang/Object" || b == "java/lang/Object") {
                    return "java/lang/Object"
                }
                return "java/lang/Object"
            }
        }
        cn.accept(cw)
        return cw.toByteArray()
    }
}