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

data class ModrinthProject(
    private val title: String,
    @SerializedName("description") private val summary: String,
    private val author: String,
    @SerializedName("icon_url") private val iconUrl: String?,
    private val gallery: List<String>,
    @SerializedName("featured_gallery") private val featuredGallery: String?,
    private val color: Int?,
    val slug: String,
    @SerializedName("project_type") val projectType: String,
) : IProject {
    @Transient
    private var projectRequest: CompletableFuture<ProjectResponse?>? = null

    @Transient
    private var membersRequest: CompletableFuture<List<Member>?>? = null

    @Transient
    private var versionsRequest: CompletableFuture<List<ModrinthVersion>>? = null

    override fun getName(): String = title
    override fun getSummary(): String = summary
    override fun getAuthor(): String = author
    override fun getIconUrl(): String? = iconUrl
    override fun getBannerUrl(): String? = featuredGallery ?: gallery.firstOrNull()
    override fun getBannerColor(): Color? = color?.let { Color(it) }
    override fun getDescription(): CompletableFuture<String> =
        fetchProject().thenApply { it?.body ?: error("Failed to fetch description.") }

    override fun getBrowserUrl(): String = "https://modrinth.com/$projectType/$slug"
    override fun getCategories(): CompletableFuture<List<String>> =
        fetchProject().thenApply {
            it?.categories?.map { c ->
                "resourcify.categories.${c.lowercase().replace(" ", "_")}"
                    .localizeOrDefault(c.capitalizeAll())
            } ?: error("Failed to fetch categories.")
        }

    override fun getExternalLinks(): CompletableFuture<Map<String, String>> = fetchProject().thenApply {
        if (it == null) error("Failed to get external links")
        mutableMapOf<String, String>().apply {
            if (!it.wikiUrl.isNullOrBlank()) put("resourcify.project.wiki".localize(), it.wikiUrl)
            if (!it.discordUrl.isNullOrBlank()) put("resourcify.project.discord".localize(), it.discordUrl)
            if (!it.sourceUrl.isNullOrBlank()) put("resourcify.project.source".localize(), it.sourceUrl)
            if (!it.issuesUrl.isNullOrBlank()) put("resourcify.project.issues".localize(), it.issuesUrl)
            it.donationUrls.forEach { donationUrl -> put(donationUrl.platform, donationUrl.url) }
        }
    }

    private fun fetchProject(): CompletableFuture<ProjectResponse?> {
        return projectRequest ?: supplyAsync {
            URL("${ModrinthService.API}/project/$slug").getJson<ProjectResponse>()
        }.apply { projectRequest = this }
    }

    override fun getMembers(): CompletableFuture<List<IMember>> = fetchMembers().thenApply { members ->
        members?.map { it.user.apply { member = it } } ?: error("Failed to fetch members.")
    }

    override fun hasGallery(): Boolean = gallery.isNotEmpty()

    override fun getGalleryImages(): CompletableFuture<List<IGalleryImage>> = fetchProject().thenApply {
        it?.gallery?.sortedBy { image -> image.ordering }
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

    data class ProjectResponse(
        val body: String,
        val categories: List<String>,
        @SerializedName("wiki_url") val wikiUrl: String?,
        @SerializedName("discord_url") val discordUrl: String?,
        @SerializedName("source_url") val sourceUrl: String?,
        @SerializedName("issues_url") val issuesUrl: String?,
        @SerializedName("donation_urls") val donationUrls: List<DonationUrl>,
        val gallery: List<GalleryImage>
    )

    data class DonationUrl(val platform: String, val url: String)

    data class Member(val user: User, val role: String)

    data class User(
        @SerializedName("username") override val name: String,
        @SerializedName("avatar_url") override val avatarUrl: String?
    ) : IMember {
        var member: Member? = null

        override val role: String
            get() = member?.role ?: "Owner"

        @Transient
        override val url: String = "https://modrinth.com/user/$name"
    }

    data class GalleryImage(
        override val url: String, override val title: String?, override val description: String?, val ordering: Int
    ) : IGalleryImage
}
