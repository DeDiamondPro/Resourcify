/*
 * This file is part of Resourcify
 * Copyright (C) 2025-2026 DeDiamondPro
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
    private val exclusiveUpperBound: String? = null, // Exclusive upper bound for loader range only, does not affect publishing
) {
    fun getName(): String {
        return name;
    }

    fun getLoaderRange(platform: Platform): String {
        return if (platform.isFabric) getFabricRange() else getForgeRange()
    }

    fun getFabricRange(): String {
        if (allowAll) return "*"
        return buildString {
            append(">=$startVersion")
            if (!openEnd && exclusiveUpperBound != null) append(" <$exclusiveUpperBound")
            else if (!openEnd) append(" <=$endVersion")
        }
    }

    fun getForgeRange(): String {
        if (allowAll) return "[1,)"
        return buildString {
            append("[$startVersion,")
            if (!openEnd && exclusiveUpperBound != null) append("$exclusiveUpperBound[")
            else if (!openEnd) append("$endVersion]")
            else append(")")
        }
    }
}