/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

data class FullModrinthProject(
    val id: String,
    val slug: String,
    @SerializedName("project_type") val projectType: String,
    val team: String,
    val title: String,
    val description: String,
    val body: String,
    val published: String,
    val updated: String,
    val status: String,
    @SerializedName("client_side") val clientSide: String,
    @SerializedName("server_side") val serverSide: String,
    val downloads: Int,
    val followers: Int,
    val categories: List<String>,
    @SerializedName("additional_categories") val additionalCategories: List<String>,
    @SerializedName("game_versions") val gameVersions: List<String>,
    val loaders: List<String>,
    val versions: List<String>,
    @SerializedName("icon_url") val iconUrl: String?,
    @SerializedName("issues_url") val issuesUrl: String?,
    @SerializedName("source_url") val sourceUrl: String?,
    @SerializedName("wiki_url") val wikiUrl: String?,
    @SerializedName("discord_url") val discordUrl: String?,
    val color: Int?
)