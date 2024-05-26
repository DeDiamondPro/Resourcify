/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
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

package dev.dediamondpro.resourcify.util

import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.utils.ReleasedDynamicTexture
import net.minecraft.client.resources.I18n
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun String.capitalizeAll(): String {
    return this.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
        .split("-").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
}

@JvmName("localizeExtension")
fun String.localize(vararg parameters: Any): String {
    return I18n.format(this, *parameters)
}

fun localize(key: String, vararg parameters: Any): String {
    return I18n.format(key, *parameters)
}

@JvmName("localizeOrDefaultExtension")
fun String.localizeOrDefault(default: String, vararg parameters: Any): String {
    val formatted = I18n.format(this, *parameters)
    return if (formatted == this) default.format(*parameters) else formatted
}

fun localizeOrDefault(key: String, default: String, vararg parameters: Any): String {
    val formatted = I18n.format(key, *parameters)
    return if (formatted == key) default.format(*parameters) else formatted
}

object Utils {
    fun drawTexture(
        matrixStack: UMatrixStack,
        texture: ReleasedDynamicTexture,
        x: Double,
        y: Double,
        textureX: Double,
        textureY: Double,
        width: Double,
        height: Double,
        textureWidth: Double,
        textureHeight: Double,
        textureMinFilter: Int = GL11.GL_NEAREST,
        textureMagFilter: Int = GL11.GL_NEAREST
    ) {
        matrixStack.push()

        UGraphics.enableBlend()
        UGraphics.enableAlpha()
        matrixStack.scale(1f, 1f, 50f)
        val glId = texture.dynamicGlId
        UGraphics.bindTexture(0, glId)
        val worldRenderer = UGraphics.getFromTessellator()
        UGraphics.configureTexture(glId) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, textureMinFilter)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, textureMagFilter)
        }

        worldRenderer.beginWithDefaultShader(
            UGraphics.DrawMode.QUADS,
            UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR
        )

        worldRenderer.pos(matrixStack, x, y + height, 0.0)
            .tex(textureX / textureWidth, (textureY + height) / textureHeight)
            .color(1f, 1f, 1f, 1f).endVertex()
        worldRenderer.pos(matrixStack, x + width, y + height, 0.0)
            .tex((textureX + width) / textureWidth, (textureY + height) / textureHeight)
            .color(1f, 1f, 1f, 1f).endVertex()
        worldRenderer.pos(matrixStack, x + width, y, 0.0)
            .tex((textureX + width) / textureWidth, textureY / textureHeight)
            .color(1f, 1f, 1f, 1f).endVertex()
        worldRenderer.pos(matrixStack, x + 0, y + 0, 0.0)
            .tex((textureX + 0) / textureWidth, (textureY + 0) / textureHeight)
            .color(1f, 1f, 1f, 1f).endVertex()
        worldRenderer.drawDirect()

        matrixStack.pop()
    }

    fun getSha1(file: File): String? {
        try {
            FileInputStream(file).use { it ->
                val digest: MessageDigest = MessageDigest.getInstance("SHA-1")
                val buffer = ByteArray(1024)
                var count: Int
                while (it.read(buffer).also { count = it } != -1) {
                    digest.update(buffer, 0, count)
                }
                val digested: ByteArray = digest.digest()
                val sb = StringBuilder()
                for (b in digested) {
                    sb.append(((b.toInt() and 0xff) + 0x100).toString(16).substring(1))
                }
                return sb.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }

    fun getShadowColor(color: Color): Color {
        val rgb = color.rgb
        return Color(rgb and 16579836 shr 2 or (rgb and -16777216))
    }
}