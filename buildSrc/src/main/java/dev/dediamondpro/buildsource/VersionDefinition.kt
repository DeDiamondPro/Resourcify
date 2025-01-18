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

class VersionDefinition(vararg pairs: Pair<String, String>) {
    private val versions = mapOf(*pairs)

    fun get(platform: Platform): String {
        // Try full platform first, then mc version
        return versions.get(platform.name) ?: versions.get(platform.versionString)
        ?: error("No version for ${platform.name}")
    }
}