/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
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

package dev.dediamondpro.resourcify.gui.update.modrinth

import com.google.gson.annotations.SerializedName
import dev.dediamondpro.resourcify.platform.Platform

data class ModrinthUpdateFormat(
    val hashes: List<String>,
    val loaders: List<String>,
    val algorithm: String = "sha512",
    @SerializedName("game_versions") val gameVersions: List<String> = listOf(Platform.getMcVersion())
)