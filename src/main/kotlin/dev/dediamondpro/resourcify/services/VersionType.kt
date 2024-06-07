/*
 * This file is part of Resourcify
 * Copyright (C) 2024 DeDiamondPro
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

package dev.dediamondpro.resourcify.services

import dev.dediamondpro.resourcify.util.localize
import java.awt.Color

enum class VersionType(val color: Color) {
    RELEASE(Color(27, 217, 106)),
    BETA(Color(255, 163, 71)),
    ALPHA(Color(255, 73, 110));

    val localizedName: String
        get() = "resourcify.version.type.${name.lowercase()}".localize()

    companion object {
        fun fromName(name: String): VersionType {
            return values().firstOrNull { it.name.equals(name, ignoreCase = true) } ?: RELEASE
        }
    }
}