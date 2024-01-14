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

data class ProjectResponse(
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
    val license: LicenseResponse,
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
    @SerializedName("donation_urls") val donationUrls: List<DonationResponse>,
    val gallery: List<GalleryResponse>,
    val color: Int?
)

data class LicenseResponse(val id: String, val name: String, val url: String?)

data class DonationResponse(val id: String, val platform: String, val url: String)

data class GalleryResponse(
    val url: String,
    val featured: Boolean,
    val title: String?,
    val description: String?,
    val created: String,
    val ordering: Int
)