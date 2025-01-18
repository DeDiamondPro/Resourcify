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
    private val startVersion: String,
    private val endVersion: String,
    private val name: String = "$startVersion-$endVersion",
    private val openEnd: Boolean = false
) {
    fun getName(): String {
        return name;
    }

    fun getLoaderRange(platform: Platform): String {
        return if (platform.isFabric) getFabricRange() else getForgeRange()
    }

    fun getFabricRange(): String {
        return ">=$startVersion" + if (!openEnd) " <=$endVersion" else ""
    }

    fun getForgeRange(): String {
        return "[$startVersion," + if (!openEnd) "$endVersion]" else ")"
    }

    fun asList(): List<String> {
        val minorVersion = startVersion.let {
            if (it.count { c -> c == '.' } == 1) it else it.substringBeforeLast(".")
        }
        val start = startVersion.let {
            if (it.count { c -> c == '.' } == 1) 0 else it.substringAfterLast(".").toInt()
        }
        val end = endVersion.let {
            if (it.count { c -> c == '.' } == 1) 0 else it.substringAfterLast(".").toInt()
        }
        val versions = mutableListOf<String>()
        for (i in start..end) {
            versions.add("$minorVersion${if (i == 0) "" else ".$i"}")
        }
        return versions
    }
}