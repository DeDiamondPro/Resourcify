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

enum class DependencyType {
    REQUIRED,
    OPTIONAL;

    fun getLocalizedName(): String {
        return when (this) {
            REQUIRED -> "resourcify.project.required_dependency".localize()
            OPTIONAL -> "resourcify.project.optional_dependency".localize()
        }
    }

    companion object {
        fun fromString(string: String): DependencyType? = when (string) {
            "required" -> REQUIRED
            "optional" -> OPTIONAL
            else -> null
        }

        fun fromCurseForgeId(id: Int): DependencyType? = when (id) {
            2 -> OPTIONAL
            3 -> REQUIRED
            else -> null
        }
    }
}