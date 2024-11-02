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

package dev.dediamondpro.resourcify.services.modrinth

import com.google.gson.annotations.SerializedName
import dev.dediamondpro.resourcify.services.IGalleryImage
import dev.dediamondpro.resourcify.services.IMember
import dev.dediamondpro.resourcify.services.IProject
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.util.*
import java.awt.Color
import java.net.URL
import java.util.concurrent.CompletableFuture

data class FullModrinthProject(
    private val id: String,
    private val slug: String,
    @SerializedName("project_type") private val projectType: String,
    private val team: String,
    private val title: String,
    private val description: String,
    private val body: String,
    private val published: String,
    private val updated: String,
    private val status: String,
    @SerializedName("client_side") private val clientSide: String,
    @SerializedName("server_side") private val serverSide: String,
    private val downloads: Int,
    private val followers: Int,
    private val categories: List<String>,
    @SerializedName("additional_categories") private val additionalCategories: List<String>,
    @SerializedName("game_versions") private val gameVersions: List<String>,
    private val loaders: List<String>,
    private val versions: List<String>,
    @SerializedName("icon_url") private val iconUrl: String?,
    @SerializedName("issues_url") private val issuesUrl: String?,
    @SerializedName("source_url") private val sourceUrl: String?,
    @SerializedName("wiki_url") private val wikiUrl: String?,
    @SerializedName("discord_url") private val discordUrl: String?,
    @SerializedName("donation_urls") private val donationUrls: List<DonationUrl>,
    private val color: Int?,
    private val gallery: List<GalleryImage>,
) : IProject {
    @Transient
    private var membersRequest: CompletableFuture<List<PartialModrinthProject.Member>?>? = null

    @Transient
    private var versionsRequest: CompletableFuture<List<ModrinthVersion>>? = null

    override fun getName(): String = title
    override fun getId(): String = id
    override fun getSummary(): String = description
    override fun getAuthor(): String = membersRequest?.getNow(null)?.firstOrNull()?.user?.name ?: ""

    override fun getBrowserUrl(): String = "https://modrinth.com/$projectType/$slug"

    override fun getDescription(): CompletableFuture<String> = supply { body }
    override fun getIconUrl(): URL? = iconUrl?.toURL()

    override fun getBannerUrl(): URL? = gallery.minByOrNull { it.ordering }?.getThumbnailUrlIfEnabled()

    override fun getBannerColor(): Color? = color?.let { Color(it) }

    override fun getCategories(): CompletableFuture<List<String>> = supply {
        categories.map { c ->
            "resourcify.categories.${c.lowercase().replace(" ", "_")}"
                .localizeOrDefault(c.capitalizeAll())
        }
    }

    override fun getExternalLinks(): CompletableFuture<Map<String, String>> = supply {
        mutableMapOf<String, String>().apply {
            if (!wikiUrl.isNullOrBlank()) put("resourcify.project.wiki".localize(), wikiUrl)
            if (!discordUrl.isNullOrBlank()) put("resourcify.project.discord".localize(), discordUrl)
            if (!sourceUrl.isNullOrBlank()) put("resourcify.project.source".localize(), sourceUrl)
            if (!issuesUrl.isNullOrBlank()) put("resourcify.project.issues".localize(), issuesUrl)
            donationUrls.forEach { donationUrl -> put(donationUrl.platform, donationUrl.url) }
        }
    }

    override fun getMembers(): CompletableFuture<List<IMember>> = fetchMembers().thenApply { members ->
        members?.map { it.user.apply { member = it } } ?: error("Failed to fetch members.")
    }

    private fun fetchMembers(): CompletableFuture<List<PartialModrinthProject.Member>?> {
        return membersRequest ?: supplyAsync {
            URL("${ModrinthService.API}/project/$slug/members").getJson<List<PartialModrinthProject.Member>>()
        }.apply { membersRequest = this }
    }

    override fun hasGallery(): Boolean = gallery.isNotEmpty()

    override fun getGalleryImages(): CompletableFuture<List<IGalleryImage>> = supply {
        gallery.sortedBy { image -> image.ordering }
    }

    override fun getVersions(): CompletableFuture<List<IVersion>> {
        return (versionsRequest ?: supplyAsync {
            URL("${ModrinthService.API}/project/$slug/version").getJson<List<ModrinthVersion>>()
                ?.filter { it.hasFile() }
                // Filter mods (jar files) out of datapack versions
                ?.filter { projectType != "mod" || it.getLoaders().contains("datapack") }
                ?: error("Failed to fetch versions.")
        }.apply { versionsRequest = this }) as CompletableFuture<List<IVersion>>
    }

    data class DonationUrl(val platform: String, val url: String)

    data class GalleryImage(
        @SerializedName("raw_url") override val url: String,
        @SerializedName("url") override val thumbnailUrl: String?,
        override val title: String?,
        override val description: String?,
        val ordering: Long
    ) : IGalleryImage
}