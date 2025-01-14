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

plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.blossom) apply false
    alias(egt.plugins.multiversionRoot)
}

preprocess {
    val fabric12001 = createNode("1.20.1-fabric", 12001, "yarn")
    val fabric12101 = createNode("1.21.1-fabric", 12101, "yarn")
    val fabric12103 = createNode("1.21.3-fabric", 12103, "yarn")

    val forge12001 = createNode("1.20.1-forge", 12001, "srg")
    val forge12101 = createNode("1.21.1-forge", 12101, "srg")
    val forge12103 = createNode("1.21.3-forge", 12103, "srg")

    val neoforge12101 = createNode("1.21.1-neoforge", 12101, "srg")
    val neoforge12103 = createNode("1.21.3-neoforge", 12103, "srg")

    fabric12101.link(fabric12103)
    fabric12001.link(fabric12101)

    forge12103.link(fabric12103)
    forge12101.link(fabric12101)
    forge12001.link(fabric12001)

    neoforge12103.link(fabric12103)
    neoforge12101.link(fabric12101)
}