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

package dev.dediamondpro.resourcify.services.modrinth

import com.google.gson.annotations.SerializedName
import dev.dediamondpro.resourcify.services.IGalleryImage
import dev.dediamondpro.resourcify.services.IProject
import dev.dediamondpro.resourcify.services.IMember
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.util.*
import java.awt.Color
import java.net.URL
import java.util.concurrent.CompletableFuture

data class PartialModrinthProject(
    private val title: String,
    @SerializedName("description") private val summary: String,
    private val author: String,
    @SerializedName("icon_url") private val iconUrl: String?,
    private val gallery: List<String>,
    @SerializedName("featured_gallery") private val featuredGallery: String?,
    private val color: Int?,
    private val slug: String,
    @SerializedName("project_type") private val projectType: String,
    private val id: String,
) : IProject {
    @Transient
    private var projectRequest: CompletableFuture<FullModrinthProject?>? = null

    @Transient
    private var membersRequest: CompletableFuture<List<Member>?>? = null

    @Transient
    private var versionsRequest: CompletableFuture<List<ModrinthVersion>>? = null

    override fun getName(): String = title
    override fun getId(): String = id
    override fun getSummary(): String = summary
    override fun getAuthor(): String = author
    override fun getIconUrl(): String? = iconUrl
    override fun getBannerUrl(): String? = featuredGallery ?: gallery.firstOrNull()
    override fun getBannerColor(): Color? = color?.let { Color(it) }
    override fun getDescription(): CompletableFuture<String> =
        fetchProject().thenApply { it?.getDescription()?.getNow(null) ?: error("Failed to fetch description.") }

    override fun getBrowserUrl(): String = "https://modrinth.com/$projectType/$slug"
    override fun getCategories(): CompletableFuture<List<String>> =
        fetchProject().thenApply {
            it?.getCategories()?.getNow(null) ?: error("Failed to fetch categories.")
        }

    override fun getExternalLinks(): CompletableFuture<Map<String, String>> = fetchProject().thenApply {
        it?.getExternalLinks()?.getNow(null) ?: error("Failed to get external links")
    }

    private fun fetchProject(): CompletableFuture<FullModrinthProject?> {
        return projectRequest ?: supplyAsync {
            URL("${ModrinthService.API}/project/$slug").getJson<FullModrinthProject>()
        }.apply { projectRequest = this }
    }

    override fun getMembers(): CompletableFuture<List<IMember>> = fetchMembers().thenApply { members ->
        members?.map { it.user.apply { member = it } } ?: error("Failed to fetch members.")
    }

    override fun hasGallery(): Boolean = gallery.isNotEmpty()

    override fun getGalleryImages(): CompletableFuture<List<IGalleryImage>> = fetchProject().thenApply {
        it?.getGalleryImages()?.getNow(null) ?: error("Failed to fetch gallery.")
    }

    override fun getVersions(): CompletableFuture<List<IVersion>> {
        return (versionsRequest ?: supplyAsync {
            URL("${ModrinthService.API}/project/$slug/version").getJson<List<ModrinthVersion>>()
                ?.filter { it.hasFile() } ?: error("Failed to fetch versions.")
        }.apply { versionsRequest = this }) as CompletableFuture<List<IVersion>>
    }

    private fun fetchMembers(): CompletableFuture<List<Member>?> {
        return membersRequest ?: supplyAsync {
            URL("${ModrinthService.API}/project/$slug/members").getJson<List<Member>>()
        }.apply { membersRequest = this }
    }

    data class Member(val user: User, val role: String)

    data class User(
        @SerializedName("username") override val name: String,
        @SerializedName("avatar_url") override val avatarUrl: String?
    ) : IMember {
        var member: Member? = null

        override val role: String
            get() = member?.role ?: "Owner"

        override val url: String
            get() = "https://modrinth.com/user/$name"
    }
}
