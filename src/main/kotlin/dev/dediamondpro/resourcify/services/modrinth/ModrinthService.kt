package dev.dediamondpro.resourcify.services.modrinth

import dev.dediamondpro.resourcify.services.ISearchData
import dev.dediamondpro.resourcify.services.IService
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.util.*
import org.apache.http.client.utils.URIBuilder
import java.net.URL
import java.util.concurrent.CompletableFuture

object ModrinthService : IService {
    const val API = "https://api.modrinth.com/v2"
    private var minecraftVersions: CompletableFuture<List<ModrinthMinecraftVersion>>? = null
    private var categories: CompletableFuture<List<ModrinthCategory>>? = null

    override fun getName(): String = "Modrinth"
    override fun search(
        query: String,
        sortBy: String,
        minecraftVersions: List<String>,
        categories: List<String>,
        offset: Int,
        type: ProjectType
    ): ISearchData? {
        return URIBuilder("${API}/search")
            .setParameter("query", query)
            .setParameter("facets", buildFacets(type, minecraftVersions, categories))
            .setParameter("limit", "20")
            .setParameter("offset", offset.toString())
            .setParameter("index", sortBy)
            .build().toURL().getJson<ModrinthSearchData>()
    }

    private fun buildFacets(type: ProjectType, minecraftVersions: List<String>, categories: List<String>): String =
        buildString {
            append("[")
            append(
                when (type) {
                    ProjectType.DATA_PACK -> "[\"project_type:mod\"],[\"categories=datapack\"]"
                    ProjectType.IRIS_SHADER -> "[\"project_type:shader\"],[\"categories=iris\"]"
                    ProjectType.OPTIFINE_SHADER -> "[\"project_type:shader\"],[\"categories=optifine\"]"
                    else -> "[\"project_type:${type.getProjectType()}\"]"
                }
            )
            if (categories.isNotEmpty()) {
                append(",")
                append(categories.joinToString(",") { "[\"categories:'${it}'\"]" })
            }
            if (minecraftVersions.isNotEmpty()) {
                append(",[")
                append(minecraftVersions.joinToString(separator = ",") { version -> "\"versions:$version\"" })
                append("]")
            }
            append("]")
        }

    override fun getMinecraftVersions(): CompletableFuture<Map<String, String>> {
        fetchMinecraftVersions()
        return minecraftVersions?.thenApply {
            it.filter { version -> version.versionType == "release" }
                .associate { version -> version.version to version.version }
        } ?: supply { emptyMap() }
    }

    private fun fetchMinecraftVersions() {
        if (minecraftVersions != null && minecraftVersions?.isDone == true && minecraftVersions?.isCompletedExceptionally == false) return
        minecraftVersions = supplyAsync {
            URL("https://api.modrinth.com/v2/tag/game_version")
                .getJson<List<ModrinthMinecraftVersion>>(useCache = false)
                ?: error("Failed to fetch Minecraft versions.")
        }
    }

    override fun getCategories(type: ProjectType): CompletableFuture<Map<String, Map<String, String>>> {
        fetchCategories()
        return categories?.thenApply {
            it.filter { category -> category.projectType == type.getProjectType() }
                .groupBy { category -> localizeCategory(category.header) }
                .mapValues { (_, categories) ->
                    categories.associate { category ->
                        category.name to localizeCategory(category.name)
                    }
                }
        } ?: supply { emptyMap() }
    }

    private fun localizeCategory(category: String): String =
        "resourcify.categories.${category.lowercase().replace(" ", "_")}"
            .localizeOrDefault(category.capitalizeAll())

    private fun fetchCategories() {
        if (categories != null && categories?.isDone == true && categories?.isCompletedExceptionally == false) return
        categories = supplyAsync {
            URL("$API/tag/category")
                .getJson<List<ModrinthCategory>>(useCache = false)
                ?: error("Failed to fetch categories.")
        }.thenApply {
            (it ?: emptyList()).sortedBy { category ->
                category.header + if (!category.name.matches(Regex("^[0-9].*"))) "\uFFFF${category.name}"
                else category.name.replace(Regex("[^0-9]"), "").toInt().toChar()
            }
        }
    }

    private fun ProjectType.getProjectType(): String = when (this) {
        ProjectType.RESOURCE_PACK -> "resourcepack"
        ProjectType.AYCY_RESOURCE_PACK -> "resourcepack"
        ProjectType.DATA_PACK -> "mod"
        ProjectType.IRIS_SHADER -> "shader"
        ProjectType.OPTIFINE_SHADER -> "shader"
    }

    override fun getSortOptions(): Map<String, String> = mapOf(
        "relevance" to "resourcify.browse.sort.relevance",
        "downloads" to "resourcify.browse.sort.downloads",
        "follows" to "resourcify.browse.sort.follows",
        "newest" to "resourcify.browse.sort.newest",
        "updated" to "resourcify.browse.sort.updated",
    )
}