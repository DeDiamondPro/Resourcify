/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

import gg.essential.gradle.util.*

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.4.21"
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
    id("com.github.johnrengelman.shadow")
    id("net.kyori.blossom")
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
    if (project.platform.isLegacyForge) launchConfigs.named("client") {
        arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
    }
    if (project.platform.isForge) forge {
        mixinConfig("mixins.${mod_id}.json")
    }

    mixin.defaultRefmapName.set("mixins.${mod_id}.refmap.json")
}

repositories {
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://repo.essential.gg/repository/maven-public/")
    maven("https://maven.dediamondpro.dev/releases")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    mavenCentral()
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    val elementaVersion = "590+markdown"
    val universalVersion = "269"
    val elementaPlatform: String? by project
    if (platform.isFabric) {
        val fabricApiVersion: String by project
        val fabricLanguageKotlinVersion: String by project
        modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
        modImplementation("net.fabricmc:fabric-language-kotlin:$fabricLanguageKotlinVersion")
        modImplementation("include"("gg.essential:elementa-${elementaPlatform ?: platform}:$elementaVersion")!!)
        modImplementation("include"("gg.essential:universalcraft-${platform}:$universalVersion")!!)
    } else if (platform.isForge) {
        val essentialPlatform: String? by project
        shade("gg.essential:elementa-${elementaPlatform ?: platform}:$elementaVersion") {
            isTransitive = false
        }
        compileOnly("gg.essential:essential-${essentialPlatform ?: platform}:4166+ge3c5b9d02")
        if (platform.isLegacyForge) {
            shade("gg.essential:loader-launchwrapper:1.1.3") {
                isTransitive = false
            }
            annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
            compileOnly("org.spongepowered:mixin:0.8.5")
        } else {
            val kotlinForForgeVersion: String by project
            runtimeOnly("thedarkcolour:kotlinforforge:$kotlinForForgeVersion")
            shade("gg.essential:universalcraft-${platform}:$universalVersion") {
                isTransitive = false
            }
        }
    }
    shade("com.github.ben-manes.caffeine:caffeine:2.9.3")
    shade("com.twelvemonkeys.imageio:imageio-webp:3.9.4")
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
    withType(Jar::class.java) {
        if (project.platform.isFabric) {
            exclude("mcmod.info", "mods.toml")
        } else {
            exclude("fabric.mod.json")
            if (project.platform.isLegacyForge) {
                exclude("mods.toml")
            } else {
                exclude("mcmod.info")
            }
        }
    }
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("dev")
        configurations = listOf(shade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        mergeServiceFiles()
        relocate("com.github.benmanes.caffeine", "dev.dediamondpro.resourcify.libs.caffeine")
        relocate("com.twelvemonkeys", "dev.dediamondpro.resourcify.libs.twelvemonkeys")
        if (platform.isForge) {
            relocate("gg.essential.elementa", "dev.dediamondpro.resourcify.libs.elementa")
            if (!platform.isLegacyForge) {
                relocate("gg.essential.universal", "dev.dediamondpro.resourcify.libs.universal")
            }
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
                        "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
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
}

fun getMcVersionStr(): String {
    return when (project.platform.mcVersionStr) {
        "1.19.2" -> "1.19.0-1.19.2"
        in listOf("1.8.9", "1.12.2", "1.19.4", "1.20") -> project.platform.mcVersionStr
        else -> "${project.platform.mcVersionStr.substringBeforeLast(".")}.x"
    }
}

fun getInternalMcVersionStr(): String {
    return when (project.platform.mcVersionStr) {
        in listOf("1.8.9", "1.12.2", "1.19.4") -> project.platform.mcVersionStr
        else -> {
            val dots = project.platform.mcVersionStr.count { it == '.' }
            if (dots == 1) "${project.platform.mcVersionStr}.x"
            else "${project.platform.mcVersionStr.substringBeforeLast(".")}.x"
        }
    }
}