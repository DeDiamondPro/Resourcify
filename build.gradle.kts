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

import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import gg.essential.gradle.util.noServerRunConfigs
import gg.essential.gradle.util.setJvmDefault

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.serialization)
    id(egt.plugins.multiversion.get().pluginId)
    id(egt.plugins.defaults.get().pluginId)
    alias(libs.plugins.shadow)
    alias(libs.plugins.blossom)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.cursegradle)
}

val mod_name: String by project
val mod_version: String by project
val mod_id: String by project

preprocess {
    vars.put("MODERN", if (project.platform.mcMinor >= 16) 1 else 0)
}

blossom {
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
    replaceToken("@VER@", mod_version)
}

version = mod_version
group = "dev.dediamondpro"
base {
    archivesName.set("$mod_name (${getMcVersionStr()}-${platform.loaderStr})")
}

tasks.compileKotlin.setJvmDefault(if (platform.mcVersion >= 11400) "all" else "all-compatibility")
loom.noServerRunConfigs()
loom {
    if (project.platform.isLegacyForge) runConfigs {
        "client" { programArgs("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker") }
    }
    if (project.platform.isForge) forge {
        mixinConfig("mixins.${mod_id}.json")
    }

    mixin.defaultRefmapName.set("mixins.${mod_id}.refmap.json")

    if (project.platform.mcVersion >= 12002) {
        accessWidenerPath = file("src/main/resources/resourcify.accesswidener")
    }
}

repositories {
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://repo.essential.gg/repository/maven-public/")
    maven("https://maven.dediamondpro.dev/releases")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://repo.spongepowered.org/maven/")
    maven("https://api.modrinth.com/maven")
    mavenCentral()
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    val elementaPlatform: String? by project
    val universalPlatform: String? by project
    if (platform.isFabric) {
        val fabricApiVersion: String by project
        val fabricLanguageKotlinVersion: String by project
        modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
        modImplementation("net.fabricmc:fabric-language-kotlin:$fabricLanguageKotlinVersion")
        modCompileOnly("gg.essential:elementa-${elementaPlatform ?: platform}:${libs.versions.elementa.get()}")
        modImplementation("include"("gg.essential:universalcraft-${universalPlatform ?: platform}:${libs.versions.universal.get()}")!!)
    } else if (platform.isForge) {
        if (platform.isLegacyForge) {
            shade(libs.bundles.kotlin) { isTransitive = false }
            shade(libs.mixin) { isTransitive = false }
            annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
        } else {
            val kotlinForForgeVersion: String by project
            implementation("thedarkcolour:kotlinforforge:$kotlinForForgeVersion")
        }
        shade("gg.essential:universalcraft-${universalPlatform ?: platform}:${libs.versions.universal.get()}") {
            isTransitive = false
        }
    }
    listOf(libs.bundles.twelvemonkeys).forEach {
        if (platform.isFabric) {
            implementation(it)
            include(it)
        } else {
            shade(it) { isTransitive = false }
        }
    }
    // Always shade elementa since we use a custom version, relocate to avoid conflicts
    shade("gg.essential:elementa-${elementaPlatform ?: platform}:${libs.versions.elementa.get()}") {
        isTransitive = false
    }

    val irisVersion: String by project
    if (!platform.isLegacyForge) modCompileOnly(
        if (platform.isFabric) "maven.modrinth:iris:$irisVersion"
        else "maven.modrinth:oculus:$irisVersion"
    )
}

tasks.processResources {
    inputs.property("id", mod_id)
    inputs.property("name", mod_name)
    val java = if (project.platform.mcMinor >= 18) {
        17
    } else {
        if (project.platform.mcMinor == 17) 16 else 8
    }
    val compatLevel = "JAVA_${java}"
    inputs.property("java", java)
    inputs.property("java_level", compatLevel)
    inputs.property("version", mod_version)
    inputs.property("mcVersionStr", project.platform.mcVersionStr)
    filesMatching(listOf("mcmod.info", "mods.toml", "fabric.mod.json")) {
        expand(
            mapOf(
                "id" to mod_id,
                "name" to mod_name,
                "java" to java,
                "java_level" to compatLevel,
                "version" to mod_version,
                "mcVersionStr" to getInternalMcVersionStr()
            )
        )
    }
}

tasks {
    withType<Jar> {
        if (project.platform.isFabric) {
            exclude("mcmod.info", "mods.toml", "pack.mcmeta")
        } else {
            exclude("fabric.mod.json", "resourcify.accesswidener")
            if (project.platform.isLegacyForge) {
                exclude("mods.toml", "pack.mcmeta")
            } else {
                exclude("mcmod.info")
            }
        }
        from(rootProject.file("LICENSE"))
        from(rootProject.file("LICENSE.LESSER"))
    }
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("dev")
        configurations = listOf(shade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        mergeServiceFiles()
        relocate("gg.essential.elementa", "dev.dediamondpro.resourcify.libs.elementa")
        if (platform.isForge) {
            relocate("com.twelvemonkeys", "dev.dediamondpro.resourcify.libs.twelvemonkeys")
            relocate("gg.essential.universal", "dev.dediamondpro.resourcify.libs.universal")
        }
    }
    remapJar {
        input.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
        finalizedBy("copyJar")
    }
    jar {
        if (project.platform.isLegacyForge) {
            manifest {
                attributes(
                    mapOf(
                        "ModSide" to "CLIENT",
                        "TweakOrder" to "0",
                        "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                        "ForceLoadAsMod" to true
                    )
                )
            }
        }
        dependsOn(shadowJar)
        archiveClassifier.set("")
        enabled = false
    }
    register<Copy>("copyJar") {
        File("${project.rootDir}/jars").mkdir()
        from(remapJar.get().archiveFile)
        into("${project.rootDir}/jars")
    }
    clean { delete("${project.rootDir}/jars") }
    project.modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("resourcify")
        versionNumber.set(mod_version)
        versionName.set("[${getMcVersionStr()}-${platform.loaderStr}] Resourcify $mod_version")
        uploadFile.set(remapJar.get().archiveFile as Any)
        gameVersions.addAll(getMcVersionList())
        if (platform.isFabric) {
            loaders.add("fabric")
            loaders.add("quilt")
        } else if (platform.isForge) {
            loaders.add("forge")
            if (platform.mcMinor >= 20) loaders.add("neoforge")
        }
        changelog.set(file("../../changelog.md").readText())
        dependencies {
            if (platform.isForge && !platform.isLegacyForge) {
                required.project("kotlin-for-forge")
            } else if (!platform.isLegacyForge) {
                required.project("fabric-api")
                required.project("fabric-language-kotlin")
            }
        }
    }
    project.curseforge {
        project(closureOf<CurseProject> {
            apiKey = System.getenv("CURSEFORGE_TOKEN")
            id = "870076"
            changelog = file("../../changelog.md")
            changelogType = "markdown"
            relations(closureOf<CurseRelation> {
                if (platform.isForge && !platform.isLegacyForge) {
                    requiredDependency("kotlin-for-forge")
                } else if (!platform.isLegacyForge) {
                    requiredDependency("fabric-api")
                    requiredDependency("fabric-language-kotlin")
                }
            })
            gameVersionStrings.addAll(getMcVersionList())
            if (platform.isFabric) {
                addGameVersion("Fabric")
                addGameVersion("Quilt")
            } else if (platform.isForge) {
                addGameVersion("Forge")
                if (platform.mcMinor >= 20) addGameVersion("NeoForge")
            }
            releaseType = "release"
            mainArtifact(remapJar.get().archiveFile, closureOf<CurseArtifact> {
                displayName = "[${getMcVersionStr()}-${platform.loaderStr}] Resourcify $mod_version"
            })
        })
        options(closureOf<Options> {
            javaVersionAutoDetect = false
            javaIntegration = false
            forgeGradleIntegration = false
        })
    }
    register("publish") {
        dependsOn(modrinth)
        dependsOn(curseforge)
    }
}

fun getMcVersionStr(): String {
    return when (project.platform.mcVersionStr) {
        in listOf("1.8.9", "1.12.2", "1.19.4") -> project.platform.mcVersionStr
        "1.18.2" -> if (platform.isFabric) "1.18.x" else "1.18.2"
        "1.19.2" -> "1.19.0-1.19.2"
        "1.20.1" -> "1.20-1.20.1"
        "1.20.2" -> "1.20.2+"
        else -> {
            val dots = project.platform.mcVersionStr.count { it == '.' }
            if (dots == 1) "${project.platform.mcVersionStr}.x"
            else "${project.platform.mcVersionStr.substringBeforeLast(".")}.x"
        }
    }
}

fun getInternalMcVersionStr(): String {
    return when (project.platform.mcVersionStr) {
        in listOf("1.8.9", "1.12.2", "1.19.4") -> project.platform.mcVersionStr
        "1.19.2" -> ">=1.19 <=1.19.2"
        "1.20.1" -> ">=1.20 <=1.20.1"
        "1.20.2" -> ">=1.20.2"
        else -> {
            val dots = project.platform.mcVersionStr.count { it == '.' }
            if (dots == 1) "${project.platform.mcVersionStr}.x"
            else "${project.platform.mcVersionStr.substringBeforeLast(".")}.x"
        }
    }
}

fun getMcVersionList(): List<String> {
    return when (project.platform.mcVersionStr) {
        "1.8.9" -> listOf("1.8.9")
        "1.12.2" -> listOf("1.12.2")
        "1.16.2" -> listOf("1.16", "1.16.1", "1.16.2", "1.16.3", "1.16.4", "1.16.5")
        "1.17.1" -> listOf("1.17", "1.17.1")
        "1.18.2" -> if (platform.isFabric) listOf("1.18", "1.18.1", "1.18.2") else listOf("1.18.2")
        "1.19.2" -> listOf("1.19", "1.19.1", "1.19.2")
        "1.19.4" -> listOf("1.19.4")
        "1.20.1" -> listOf("1.20", "1.20.1")
        "1.20.2" -> listOf("1.20.2")
        else -> error("Unknown version")
    }
}