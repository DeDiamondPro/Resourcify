/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2025 DeDiamondPro
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

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net")
    }
    dependencyResolutionManagement.versionCatalogs.create("libs")
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7"
}

val platforms = listOf(
    "1.20.1-forge",
    "1.20.1-fabric",
    "1.21.1-forge",
    "1.21.1-neoforge",
    "1.21.1-fabric",
    "1.21.4-forge",
    "1.21.4-neoforge",
    "1.21.4-fabric",
    "1.21.5-forge",
    "1.21.5-neoforge",
    "1.21.5-fabric",
    "1.21.8-fabric",
)

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject) {
        for (version in platforms) {
            vers(version, version.split('-')[0])
        }
        vcsVersion = "1.21.8-fabric"
    }
}

val mod_name: String by settings
rootProject.name = mod_name