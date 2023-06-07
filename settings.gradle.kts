/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License Version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
    }
    plugins {
        val egtVersion = "0.1.15"
        id("gg.essential.multi-version.root") version egtVersion
    }
}

val mod_name: String by settings

rootProject.name = mod_name
rootProject.buildFileName = "root.gradle.kts"

listOf(
    "1.8.9-forge",
    "1.12.2-forge",
    "1.16.2-forge",
    "1.16.2-fabric",
    "1.17.1-forge",
    "1.17.1-fabric",
    "1.18.1-forge",
    "1.18.1-fabric",
    "1.19.2-fabric",
    "1.19.2-forge",
    "1.19.4-forge",
    "1.19.4-fabric",
    "1.20.0-fabric"
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}