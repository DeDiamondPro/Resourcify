package dev.dediamondpro.resourcify.services.curseforge

import dev.dediamondpro.resourcify.services.IGalleryImage
import dev.dediamondpro.resourcify.services.IProject
import dev.dediamondpro.resourcify.services.IMember
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.util.*
import java.net.URL
import java.util.concurrent.CompletableFuture

data class CurseForgeProject(
    private val id: Int,
    private val name: String,
    private val summary: String,
    private val authors: List<Author>,
    private val logo: Logo?,
    private val screenshots: List<ScreenShot>,
    private val links: Links,
    private val categories: List<Category>,
    private val allowModDistribution: Boolean?,
) : IProject {
    @Transient
    private var descriptionRequest: CompletableFuture<String>? = null

    @Transient
    private var versionsRequest: CompletableFuture<List<CurseForgeVersion>>? = null

    override fun getName(): String = name
    override fun getSummary(): String = summary
    override fun getAuthor(): String = authors.firstOrNull()?.name ?: ""
    override fun getIconUrl(): String? = logo?.url
    override fun getBannerUrl(): String? = screenshots.firstOrNull()?.url

    override fun getDescription(): CompletableFuture<String> {
        return descriptionRequest ?: supplyAsync {
            URL("${CurseForgeService.API}/mods/$id/description")
                .getJson<Description>(headers = mapOf("x-api-key" to CurseForgeService.API_KEY))
                ?.data ?: error("Failed to fetch description.")
        }.apply { descriptionRequest = this }
    }

    override fun getBrowserUrl(): String = links.websiteUrl

    override fun getCategories(): CompletableFuture<List<String>> = supply {
        categories.map {
            "resourcify.categories.${
                it.name.lowercase().replace(" ", "_")
            }".localizeOrDefault(it.name.capitalizeAll())
        }
    }

    fun getInternalCategories() = categories

    override fun getExternalLinks(): CompletableFuture<Map<String, String>> = supply {
        mutableMapOf<String, String>().apply {
            if (!links.wikiUrl.isNullOrBlank()) put("resourcify.project.wiki".localize(), links.wikiUrl)
            if (!links.sourceUrl.isNullOrBlank()) put("resourcify.project.source".localize(), links.sourceUrl)
            if (!links.issuesUrl.isNullOrBlank()) put("resourcify.project.issues".localize(), links.issuesUrl)
        }
    }

    override fun getMembers(): CompletableFuture<List<IMember>> = supply { authors }
    override fun hasGallery(): Boolean = screenshots.isNotEmpty()
    override fun getGalleryImages(): CompletableFuture<List<IGalleryImage>> = supply { screenshots }
    override fun canBeInstalled(): Boolean = allowModDistribution ?: true
    override fun getVersions(): CompletableFuture<List<IVersion>> {
        return (versionsRequest ?: supplyAsync {
            URL("${CurseForgeService.API}/mods/$id/files")
                .getJson<Versions>(headers = mapOf("x-api-key" to CurseForgeService.API_KEY))
                ?.data?.filter { it.hasDownloadUrl() } ?: error("Failed to fetch versions.")
        }.apply { versionsRequest = this }) as CompletableFuture<List<IVersion>>
    }

    data class Author(override val name: String, override val url: String) : IMember {
        override val role: String
            get() = "Author"

        override val avatarUrl: String? = null
    }

    data class Logo(val url: String)

    data class ScreenShot(
        override val url: String, override val title: String, override val description: String
    ) : IGalleryImage

    data class Description(val data: String)

    data class Links(val websiteUrl: String, val wikiUrl: String?, val sourceUrl: String?, val issuesUrl: String?)

    data class Category(val name: String, val slug: String, val id: Int)

    data class Versions(val data: List<CurseForgeVersion>)
}