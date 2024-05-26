package dev.dediamondpro.resourcify.services.curseforge

import dev.dediamondpro.resourcify.services.ISearchData
import dev.dediamondpro.resourcify.services.IService
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.util.*
import org.apache.http.client.utils.URIBuilder
import java.net.URL
import java.util.concurrent.CompletableFuture

object CurseForgeService : IService {
    const val API = "https://api.curseforge.com/v1"

    /**
     * This API key should only be used by Resourcify, you are not allowed to use it in your own project or fork,
     * if you want to use the CurseForge API in your own project, you should apply for your own API key.
     */
    const val API_KEY = "\$2a\$10\$7rGm5tkZjhmJr70sf0zIK.RK5iSPBlFuPAUCF7hbogBtrykSMihtS"
    private var minecraftVersions: CompletableFuture<List<CurseForgeMinecraftVersions>>? = null
    private var categories: CompletableFuture<List<CurseForgeCategory>>? = null

    override fun getName(): String = "CurseForge"

    override fun search(
        query: String,
        sortBy: String,
        minecraftVersions: List<String>,
        categories: List<String>,
        offset: Int,
        type: ProjectType
    ): ISearchData? {
        return URIBuilder("$API/mods/search")
            .apply {
                addParameter("searchFilter", query)
                addParameter("gameId", "432")
                addParameter("pageSize", "20")
                addParameter("sortField", sortBy)
                addParameter("sortOrder", "desc")
                addParameter("index", offset.toString())
                addParameter("classId", type.getClassId().toString())
                addParameter("categoryIds", "[${categories.joinToString(",")}]")
                if (minecraftVersions.isNotEmpty()) {
                    addParameter("gameVersionTypeId", minecraftVersions.first().split("+")[0])
                }
            }.build().toURL().getJson<CurseForgeSearchData>(headers = mapOf("x-api-key" to API_KEY))
    }

    override fun getMinecraftVersions(): CompletableFuture<Map<String, String>> {
        fetchMinecraftVersions()
        return minecraftVersions?.thenApply {
            // Add the name to the id because the id is not unique
            it.associate { version -> "${version.id}+${version.name}" to version.name }
        } ?: supply { emptyMap() }
    }

    private fun fetchMinecraftVersions() {
        if (minecraftVersions != null && minecraftVersions?.isDone == true && minecraftVersions?.isCompletedExceptionally == false) return
        minecraftVersions = supplyAsync {
            URL("$API/minecraft/version")
                .getJson<CurseForgeMinecraftVersionsResponse>(
                    headers = mapOf("x-api-key" to API_KEY),
                    useCache = false
                )?.data ?: error("Failed to fetch Minecraft versions.")
        }
    }

    override fun canSelectMultipleMinecraftVersions(): Boolean = false

    override fun getCategories(type: ProjectType): CompletableFuture<Map<String, Map<String, String>>> {
        fetchCategories()
        return categories?.thenApply {
            val classId = type.getClassId()
            mapOf("resourcify.categories.categories".localize() to
                    it.filter { category -> category.classId == classId }.sortedBy { category -> category.name }
                        .associate { category ->
                            category.id.toString() to "resourcify.categories.${
                                category.name.lowercase().replace(" ", "_")
                            }".localizeOrDefault(category.name.capitalizeAll())
                        })
        } ?: supply { emptyMap() }
    }

    private fun fetchCategories() {
        if (categories != null && categories?.isDone == true && categories?.isCompletedExceptionally == false) return
        categories = supplyAsync {
            URL("$API/categories?gameId=432")
                .getJson<CurseForgeCategoryResponse>(
                    headers = mapOf("x-api-key" to API_KEY),
                    useCache = false
                )?.data ?: error("Failed to fetch categories.")
        }
    }

    private fun ProjectType.getClassId(): Int = when (this) {
        ProjectType.RESOURCE_PACK -> 12
        ProjectType.AYCY_RESOURCE_PACK -> 12
        ProjectType.DATA_PACK -> 6945
        ProjectType.IRIS_SHADER -> 6552
        ProjectType.OPTIFINE_SHADER -> 6552
    }

    override fun getSortOptions(): Map<String, String> = mapOf(
        "1" to "resourcify.browse.sort.relevance",
        "6" to "resourcify.browse.sort.downloads",
        "11" to "resourcify.browse.sort.newest",
        "3" to "resourcify.browse.sort.updated",
    )
}