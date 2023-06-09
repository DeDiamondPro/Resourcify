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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectResponse(
    val id: String,
    val slug: String,
    @SerialName("project_type") val projectType: String,
    val team: String,
    val title: String,
    val description: String,
    val body: String,
    val published: String,
    val updated: String,
    val status: String,
    val license: LicenseResponse,
    @SerialName("client_side") val clientSide: String,
    @SerialName("server_side") val serverSide: String,
    val downloads: Int,
    val followers: Int,
    val categories: List<String>,
    @SerialName("additional_categories") val additionalCategories: List<String>,
    @SerialName("game_versions") val gameVersions: List<String>,
    val loaders: List<String>,
    val versions: List<String>,
    @SerialName("icon_url") val iconUrl: String?,
    @SerialName("issues_url") val issuesUrl: String?,
    @SerialName("source_url") val sourceUrl: String?,
    @SerialName("wiki_url") val wikiUrl: String?,
    @SerialName("discord_url") val discordUrl: String?,
    @SerialName("donation_urls") val donationUrls: List<DonationResponse>,
    val gallery: List<GalleryResponse>,
    val color: Int?
)

@Serializable
data class LicenseResponse(val id: String, val name: String, val url: String?)

@Serializable
data class DonationResponse(val id: String, val platform: String, val url: String)

@Serializable
data class GalleryResponse(
    val url: String,
    val featured: Boolean,
    val title: String?,
    val description: String?,
    val created: String,
    val ordering: Int
)