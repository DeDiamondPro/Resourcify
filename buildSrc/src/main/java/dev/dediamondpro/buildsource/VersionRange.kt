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

package dev.dediamondpro.buildsource

class VersionRange(
    val startVersion: String,
    val endVersion: String,
    private val name: String = "$startVersion-$endVersion",
    private val openEnd: Boolean = false,
    private val allowAll: Boolean = false, // Mostly used for pre releases and release candidates
) {
    fun getName(): String {
        return name;
    }

    fun getLoaderRange(platform: Platform): String {
        return if (platform.isFabric) getFabricRange() else getForgeRange()
    }

    fun getFabricRange(): String {
        if (allowAll) return "*"
        return ">=$startVersion" + if (!openEnd) " <=$endVersion" else ""
    }

    fun getForgeRange(): String {
        if (allowAll) return "[1,)"
        return "[$startVersion," + if (!openEnd) "$endVersion]" else ")"
    }
}