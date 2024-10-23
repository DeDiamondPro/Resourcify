/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.blossom) apply false
    alias(egt.plugins.multiversionRoot)
}

preprocess {
    val forge10809 = createNode("1.8.9-forge", 10809, "srg")
    val forge11202 = createNode("1.12.2-forge", 11202, "srg")
    val forge11602 = createNode("1.16.2-forge", 11602, "srg")
    val forge11801 = createNode("1.18.2-forge", 11802, "srg")
    val forge11902 = createNode("1.19.2-forge", 11902, "srg")
    val forge11904 = createNode("1.19.4-forge", 11904 , "srg")
    val forge12001 = createNode("1.20.1-forge", 12001, "srg")
    val forge12004 = createNode("1.20.4-forge", 12004, "srg")
    val forge12101 = createNode("1.21.1-forge", 12101, "srg")

    val neoforge12004 = createNode("1.20.4-neoforge", 12004, "srg")
    val neoforge12006 = createNode("1.20.6-neoforge", 12006, "srg")
    val neoforge12101 = createNode("1.21.1-neoforge", 12101, "srg")

    val fabric11602 = createNode("1.16.2-fabric", 11602, "yarn")
    val fabric11801 = createNode("1.18.2-fabric", 11802, "yarn")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")
    val fabric11904 = createNode("1.19.4-fabric", 11904, "yarn")
    val fabric12001 = createNode("1.20.1-fabric", 12001, "yarn")
    val fabric12004 = createNode("1.20.4-fabric", 12004, "yarn")
    val fabric12006 = createNode("1.20.6-fabric", 12006, "yarn")
    val fabric12101 = createNode("1.21.1-fabric", 12101, "yarn")
    val fabric12103 = createNode("1.21.3-fabric", 12103, "yarn")

    forge11202.link(forge10809, file("versions/1.8.9-forge-1.12.2-forge"))
    forge11602.link(forge11202, file("versions/1.12.2-forge-1.16.2-forge"))
    forge11801.link(fabric11801)
    forge11902.link(fabric11902)
    forge11904.link(fabric11904)
    forge12001.link(fabric12001)
    forge12004.link(forge12001)
    forge12101.link(fabric12101)

    neoforge12004.link(forge12004)
    neoforge12006.link(fabric12006)
    neoforge12101.link(fabric12101)

    fabric11602.link(forge11602)
    fabric11801.link(fabric11602)
    fabric11902.link(fabric11801)
    fabric11904.link(fabric11902)
    fabric12001.link(fabric11904)
    fabric12004.link(fabric12001)
    fabric12006.link(fabric12004)
    fabric12101.link(fabric12006)
    fabric12103.link(fabric12101)
}