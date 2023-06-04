/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val hits: List<ProjectObject>,
    val offset: Int,
    val limit: Int,
    @SerialName("total_hits") val totalHits: Int
)

@Serializable
data class ProjectObject(
    @SerialName("project_id") val projectId: String,
    @SerialName("project_type") val projectType: String,
    val slug: String,
    val author: String,
    val title: String,
    val description: String,
    val categories: List<String>,
    @SerialName("display_categories") val displayCategories: List<String>,
    val versions: List<String>,
    val downloads: Int,
    val follows: Int,
    @SerialName("icon_url") val iconUrl: String?,
    @SerialName("date_created") val dateCreated: String,
    @SerialName("date_modified") val dateModified: String,
    @SerialName("latest_version") val latestVersion: String,
    val license: String,
    @SerialName("client_side") val clientSide: String,
    @SerialName("server_side") val serverSide: String,
    val gallery: List<String>,
    @SerialName("featured_gallery") val featuredGallery: String?,
    val color: Int?
) {
    val browserUrl = buildString {
        append("https://modrinth.com/$projectType/$slug")
    }
}
