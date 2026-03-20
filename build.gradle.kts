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

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.dediamondpro.buildsource.Platform
import dev.dediamondpro.buildsource.VersionDefinition
import dev.dediamondpro.buildsource.VersionRange
import net.fabricmc.loom.task.RemapJarTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
    alias(libs.plugins.arch.loom)
    alias(libs.plugins.publishing)
}

buildscript {
    // Set loom platform to correct loader
    extra["loom.platform"] = project.name.split('-')[1]
    if (project.name[0] != '1') {
        extra.set("fabric.loom.disableObfuscation", "true")
    }
}

val mcPlatform = Platform.fromProject(project)

val mod_name: String by project
val mod_version: String by project
val mod_id: String by project

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net")
    maven("https://maven.parchmentmc.org")
    maven("https://maven.minecraftforge.net")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.dediamondpro.dev/releases")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://repo.essential.gg/repository/maven-public/")
}

stonecutter {
    constants["fabric"] = mcPlatform.isFabric
    constants["forge"] = mcPlatform.isForge
    constants["neoforge"] = mcPlatform.isNeoForge
    constants["forgelike"] = mcPlatform.isForgeLike

    swaps["mod_name"] = "\"$mod_name\""
    swaps["mod_id"] = "\"$mod_id\""
    swaps["mod_version"] = "\"$mod_version\""
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val shadeModImplementation: Configuration by configurations.creating {
    val parent = if (mcPlatform.isObfuscated) "modImplementation" else "implementation"
    configurations.getByName(parent).extendsFrom(this)
}

// Version definitions
val mcVersion = VersionDefinition( // Used for pre releases and release candidates
    "26.1" to "26.1-rc-2",
    default = mcPlatform.versionString
)
val compatibleMcVersion = VersionDefinition(
    "1.20.1" to VersionRange("1.20", "1.20.1", name = "1.20.1"),
    "1.21.1" to VersionRange("1.21", "1.21.1", name = "1.21.1"),
    "1.21.4" to VersionRange("1.21.2", "1.21.4", name = "1.21.4"),
    // NeoForge changed stuff going from .3 to .4
    "1.21.4-neoforge" to VersionRange("1.21.4", "1.21.4", name = "1.21.4"),
    "1.21.5" to VersionRange("1.21.5", "1.21.5", name = "1.21.5"),
    "1.21.8" to VersionRange("1.21.6", "1.21.8", name = "1.21.8"),
    "1.21.10" to VersionRange("1.21.9", "1.21.10", name = "1.21.10"),
    "1.21.11" to VersionRange("1.21.11", "1.21.11", name = "1.21.11"),
    "26.1" to VersionRange("26.1", "26.2", inclusive = false, name = "26.1", allowAll = true) // TODO: remove allow all for final release
)
val javaVersion = VersionDefinition(
    "1.20.1" to "17",
    "26.1" to "25",
    default = "21",
)
val parchmentVersion = VersionDefinition(
    "1.20.1" to "1.20.1:2023.09.03",
    "1.21.1" to "1.21.1:2024.11.17",
    "1.21.4" to "1.21.4:2025.03.23",
    "1.21.5" to "1.21.5:2025.06.15",
    "1.21.8" to "1.21.8:2025.09.14",
)
val fabricApiVersion = VersionDefinition(
    "1.20.1" to "0.92.3+1.20.1",
    "1.21.1" to "0.114.0+1.21.1",
    "1.21.4" to "0.118.0+1.21.4",
    "1.21.5" to "0.119.4+1.21.5",
    "1.21.8" to "0.129.0+1.21.8",
    "1.21.10" to "0.136.0+1.21.10",
    "1.21.11" to "0.139.4+1.21.11",
    "26.1" to "0.143.14+26.1",
)
val modMenuVersion = VersionDefinition(
    "1.20.1" to "7.2.2",
    "1.21.1" to "11.0.3",
    "1.21.4" to "13.0.2",
    "1.21.5" to "14.0.0",
    "1.21.8" to "15.0.0",
    "1.21.10" to "16.0.0-rc.1",
    "1.21.11" to "17.0.0-alpha.1",
    "26.1" to "18.0.0-alpha.6",
)
val neoForgeVersion = VersionDefinition(
    "1.21.1" to "21.1.95",
    "1.21.4" to "21.4.124",
    "1.21.5" to "21.5.95",
    "1.21.8" to "21.8.49",
)
val minimumNeoForgeVersion = VersionDefinition(
    // We need this version or higher on 1.21.4, on other versions we don't care
    "1.21.4" to "[21.4.84-beta,)",
    default = "[1,)"
)
val forgeVersion = VersionDefinition(
    "1.20.1" to "1.20.1-47.3.0",
    "1.21.1" to "1.21.1-52.0.40",
    "1.21.4" to "1.21.4-54.1.0",
    "1.21.5" to "1.21.5-55.0.4"
)
val kotlinForForgeVersion = VersionDefinition(
    "1.20.1" to "4.11.0",
    "1.21.1" to "5.7.0",
    "1.21.4" to "5.7.0",
    "1.21.5" to "5.7.0",
    "1.21.8" to "5.9.0",
)
val universalVersion = VersionDefinition(
    "1.21.1" to "1.21",
    "1.21.8" to "1.21.7",
    "1.21.10" to "1.21.9",
    default = mcPlatform.versionString
).let { VersionDefinition(default = "${it.get(mcPlatform)}-${mcPlatform.loaderString}:466") }

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion.get(mcPlatform)}")

    if (mcPlatform.isObfuscated) {
        @Suppress("UnstableApiUsage")
        add("mappings", loom.layered {
            officialMojangMappings()
            parchmentVersion.getOrNull(mcPlatform)?.let {
                parchment("org.parchmentmc.data:parchment-$it@zip")
            }
        })
    }

    if (mcPlatform.isFabric) {
        val modImpl = if (mcPlatform.isObfuscated) "modImplementation" else "implementation"
        add(modImpl, "net.fabricmc:fabric-loader:0.18.4")
        add(modImpl, "net.fabricmc:fabric-language-kotlin:${libs.versions.fabric.language.kotlin.get()}")
        add(modImpl, "net.fabricmc.fabric-api:fabric-api:${fabricApiVersion.get(mcPlatform)}")
        add(modImpl, "com.terraformersmc:modmenu:${modMenuVersion.get(mcPlatform)}")
    } else if (mcPlatform.isNeoForge) {
        "neoForge"("net.neoforged:neoforge:${neoForgeVersion.get(mcPlatform)}")
        implementation("thedarkcolour:kotlinforforge-neoforge:${kotlinForForgeVersion.get(mcPlatform)}")
    } else if (mcPlatform.isForge) {
        "forge"("net.minecraftforge:forge:${forgeVersion.get(mcPlatform)}")
        implementation("thedarkcolour:kotlinforforge:${kotlinForForgeVersion.get(mcPlatform)}")
    }

    // Always shade and relocate universalcraft to avoid any conflict with essential (even on fabric)
    shadeModImplementation("gg.essential:universalcraft-${universalVersion.get(mcPlatform)}") {
        isTransitive = false
    }
    // Always shade elementa since we use a custom version, relocate to avoid conflicts
    shadeModImplementation("gg.essential:elementa:DIAMOND-13") {
        isTransitive = false
    }
    // Since elementa is relocated, and MineMark doesn't guarantee backwards compatibility, we need to shade this
    shade(libs.bundles.markdown) {
        isTransitive = false
    }
}

val accessWidener = when {
    mcPlatform.version >= 26_00_00 -> "26.1.resourcify"
    mcPlatform.version >= 1_21_09 -> "1.21.9.resourcify"
    mcPlatform.minor == 21 -> "1.21.resourcify"
    else -> "1.20.resourcify"
}
val mixinPath = when {
    mcPlatform.version >= 1_21_11 -> "mixins.resourcify-1.21.11.json"
    else -> "mixins.resourcify.json"
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/$accessWidener.accesswidener")

    if (mcPlatform.isForge) forge {
        convertAccessWideners.set(true)
        mixinConfig(mixinPath)
    }

    runConfigs["client"].isIdeConfigGenerated = true
}

if (mcPlatform.isForge) configurations.configureEach {
    resolutionStrategy.force("net.sf.jopt-simple:jopt-simple:5.0.4")
}

base.archivesName.set(
    "$mod_name (${
        compatibleMcVersion.get(mcPlatform).getName().replace("/", "-")
    }-${mcPlatform.loaderString})-$mod_version"
)

val outputJar = if (mcPlatform.isObfuscated) {
    tasks.named<RemapJarTask>("remapJar").flatMap { it.archiveFile }
} else {
    tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile }
}

publishMods {
    file.set(outputJar)
    displayName.set(
        "[${
            compatibleMcVersion.get(mcPlatform).getName()
        }-${mcPlatform.loaderString}] $mod_name $mod_version"
    )
    version.set(mod_version)
    changelog.set(rootProject.file("changelog.md").readText())
    type.set(STABLE)

    modLoaders.add(mcPlatform.loaderString)

    curseforge {
        projectId.set("870076")
        accessToken.set(System.getenv("CURSEFORGE_TOKEN"))

        minecraftVersionRange {
            start = compatibleMcVersion.get(mcPlatform).startVersion
            end = compatibleMcVersion.get(mcPlatform).endVersion
        }

        if (mcPlatform.isFabric) {
            requires("fabric-api", "fabric-language-kotlin")
            optional("modmenu")
        } else if (mcPlatform.isForgeLike) {
            requires("kotlin-for-forge")
        }
    }
    modrinth {
        projectId.set("RLzHAoZe")
        accessToken.set(System.getenv("MODRINTH_TOKEN"))

        minecraftVersionRange {
            start = compatibleMcVersion.get(mcPlatform).startVersion
            end = compatibleMcVersion.get(mcPlatform).endVersion
        }

        if (mcPlatform.isFabric) {
            requires("fabric-api", "fabric-language-kotlin")
            optional("modmenu")
        } else if (mcPlatform.isForgeLike) {
            requires("kotlin-for-forge")
        }
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set(if (mcPlatform.isObfuscated) "dev" else "")
        configurations = listOf(shade, shadeModImplementation)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        mergeServiceFiles()
        relocate("gg.essential.universal", "dev.dediamondpro.resourcify.libs.universal")
        relocate("gg.essential.elementa", "dev.dediamondpro.resourcify.libs.elementa")
        relocate("dev.dediamondpro.minemark", "dev.dediamondpro.resourcify.libs.minemark")
        relocate("org.commonmark", "dev.dediamondpro.resourcify.libs.commonmark")
        relocate("org.ccil.cowan.tagsoup", "dev.dediamondpro.resourcify.libs.tagsoup")
    }
    if (mcPlatform.isObfuscated) {
        named<RemapJarTask>("remapJar") {
            input.set(named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
            finalizedBy("copyJar")
            if (mcPlatform.isNeoForge) {
                atAccessWideners.add("$accessWidener.accesswidener")
            }
        }
    } else {
        named("shadowJar") { finalizedBy("copyJar") }
        named<Jar>("jar") { enabled = false }
    }
    register<Copy>("copyJar") {
        File("${project.rootDir}/jars").mkdir()
        from(outputJar)
        into("${project.rootDir}/jars")
    }
    clean { delete("${project.rootDir}/jars") }
    processResources {
        val properties = mapOf(
            "id" to mod_id,
            "name" to mod_name,
            "version" to mod_version,
            "aw" to accessWidener,
            "mixinPath" to mixinPath,
            "mcVersion" to compatibleMcVersion.get(mcPlatform).getLoaderRange(mcPlatform),
            "minNeoForgeVersion" to minimumNeoForgeVersion.get(mcPlatform)
        )

        properties.forEach { (k, v) -> inputs.property(k, v) }
        filesMatching(listOf("mcmod.info", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "fabric.mod.json")) {
            expand(properties)
        }

        if (!mcPlatform.isFabric) exclude("fabric.mod.json")
        if (!mcPlatform.isForgeLike) exclude("pack.mcmeta")
        if (!mcPlatform.isForge && (!mcPlatform.isNeoForge || mcPlatform.version >= 12005)) exclude("META-INF/mods.toml")
        if (!mcPlatform.isNeoForge || mcPlatform.version < 12005) exclude("META-INF/neoforge.mods.toml")
        // Exclude all access wideners and mixin configs except the active ones
        listOf("1.20.resourcify", "1.21.resourcify", "1.21.9.resourcify", "26.1.resourcify")
            .filter { it != accessWidener }
            .forEach { exclude("$it.accesswidener") }
        listOf("mixins.resourcify.json", "mixins.resourcify-1.21.11.json")
            .filter { it != mixinPath }
            .forEach { exclude(it) }
    }
    withType<Jar> {
        from(rootProject.file("LICENSE"))
        from(rootProject.file("LICENSE.LESSER"))
    }
}

configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.get(mcPlatform)))
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(javaVersion.get(mcPlatform)))
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}
