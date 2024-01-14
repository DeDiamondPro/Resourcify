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

package dev.dediamondpro.resourcify.modrinth

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    val hits: List<ProjectObject>,
    val offset: Int,
    val limit: Int,
    @SerializedName("total_hits") val totalHits: Int
)

data class ProjectObject(
    @SerializedName("project_id") val projectId: String,
    @SerializedName("project_type") val projectType: String,
    val slug: String,
    val author: String,
    val title: String,
    val description: String,
    val categories: List<String>,
    @SerializedName("display_categories") val displayCategories: List<String>,
    val versions: List<String>,
    val downloads: Int,
    val follows: Int,
    @SerializedName("icon_url") val iconUrl: String?,
    @SerializedName("date_created") val dateCreated: String,
    @SerializedName("date_modified") val dateModified: String,
    @SerializedName("latest_version") val latestVersion: String,
    val license: String,
    @SerializedName("client_side") val clientSide: String,
    @SerializedName("server_side") val serverSide: String,
    val gallery: List<String>,
    @SerializedName("featured_gallery") val featuredGallery: String?,
    val color: Int?
) {
    val browserUrl
        get() = "https://modrinth.com/$projectType/$slug"
}
