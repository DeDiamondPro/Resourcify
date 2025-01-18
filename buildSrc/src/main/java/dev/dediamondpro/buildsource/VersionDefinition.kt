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

class VersionDefinition<T>(vararg pairs: Pair<String, T>, private val default: T? = null) {
    private val versions = mapOf(*pairs)

    fun getOrNull(platform: Platform): T? {
        // Try full platform first, then mc version
        return versions[platform.name] ?: versions[platform.versionString] ?: default
    }

    fun get(platform: Platform): T {
        return this.getOrNull(platform) ?: error("No version for ${platform.name}")
    }

    fun hasForPlatform(platform: Platform): Boolean {
        return this.getOrNull(platform) != null
    }
}