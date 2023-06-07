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

plugins {
    kotlin("jvm") version "1.6.10" apply false
    id("net.kyori.blossom") version "1.3.0" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("gg.essential.multi-version.root")
}

preprocess {
    val forge10809 = createNode("1.8.9-forge", 10809, "srg")
    val forge11202 = createNode("1.12.2-forge", 11202, "srg")
    val forge11602 = createNode("1.16.2-forge", 11602, "srg")
    val forge11701 = createNode("1.17.1-forge", 11701, "srg")
    val forge11801 = createNode("1.18.1-forge", 11801, "srg")
    val forge11902 = createNode("1.19.2-forge", 11902, "srg")
    val forge11904 = createNode("1.19.4-forge", 11904, "srg")

    val fabric11602 = createNode("1.16.2-fabric", 11602, "yarn")
    val fabric11701 = createNode("1.17.1-fabric", 11701, "yarn")
    val fabric11801 = createNode("1.18.1-fabric", 11801, "yarn")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")
    val fabric11904 = createNode("1.19.4-fabric", 11904, "yarn")
    val fabric12000 = createNode("1.20.0-fabric", 12000, "yarn")

    forge11202.link(forge10809)
    forge11602.link(forge11202, file("versions/1.12.2-forge-1.16.2-forge"))
    forge11701.link(fabric11701)
    forge11801.link(fabric11801)
    forge11902.link(fabric11902)
    forge11904.link(fabric11904)

    fabric11602.link(forge11602)
    fabric11701.link(fabric11602)
    fabric11801.link(fabric11701)
    fabric11902.link(fabric11801)
    fabric11904.link(fabric11902)
    fabric12000.link(fabric11904)
}