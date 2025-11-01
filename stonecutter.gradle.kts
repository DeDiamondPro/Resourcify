/*
 * This file is part of Resourcify
 * Copyright (C) 2025 DeDiamondPro
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
    id("dev.kikugie.stonecutter")
    alias(libs.plugins.arch.loom) apply false
}
stonecutter active "1.21.10-fabric" /* [SC] DO NOT EDIT */

stonecutter tasks {
    val ordering = versionComparator.thenComparingInt {
        if (it.metadata.project.endsWith("fabric")) 2
        else if (it.metadata.project.endsWith("neoforge")) 1
        else 0
    }
    order("publishModrinth", ordering)
    order("publishCurseforge", ordering)
}
