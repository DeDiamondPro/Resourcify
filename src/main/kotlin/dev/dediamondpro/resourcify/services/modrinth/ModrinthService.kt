/*
 * This file is part of Resourcify
 * Copyright (C) 2024-2025 DeDiamondPro
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

import dev.dediamondpro.resourcify.services.*
import dev.dediamondpro.resourcify.util.*
import org.apache.http.client.utils.URIBuilder
import java.io.File
import java.net.URI
import java.net.URL
import java.util.concurrent.CompletableFuture

object ModrinthService : IService {
    const val API = "https://api.modrinth.com/v2"
    private var minecraftVersions: CompletableFuture<List<ModrinthMinecraftVersion>>? = null
    private var categories: CompletableFuture<List<ModrinthCategory>>? = null

    override fun getName(): String = "Modrinth"
    override fun isProjectTypeSupported(type: ProjectType) = type.getProjectType() != null

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
                    ProjectType.DATA_PACK -> "[\"project_type:datapack\"]"
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

    override fun canFetchProjectUrl(uri: URI): Boolean {
        return uri.host == "modrinth.com"
    }

    override fun fetchProjectFromUrl(uri: URI): Pair<ProjectType, CompletableFuture<IProject?>>? {
        val path = uri.path.removePrefix("/").split("/")
        if (path.size < 2) {
            return null
        }
        val type = when (path[0]) {
            "resourcepack" -> ProjectType.RESOURCE_PACK
            "datapack" -> ProjectType.DATA_PACK
            "shaders" -> ProjectType.IRIS_SHADER // Whether its iris or optifine doesn't matter here
            "mod", "modpack", "plugin" -> ProjectType.UNKNOWN
            else -> return null // Probably not a project url
        }
        val url = "$API/project/${path[1]}".toURL() ?: return null
        return type to supplyAsync {
            url.getJson<FullModrinthProject>()
        }
    }

    override fun getProjectsFromIds(ids: List<String>): Map<String, IProject> {
        val idString = ids.joinToString(",", "[", "]") { "\"${it}\"" }
        return URIBuilder("${API}/projects").setParameter("ids", idString)
            .build().toURL().getJson<List<FullModrinthProject>>()!!
            .associateBy { project -> ids.first { project.getId() == it } }
    }

    override fun getUpdates(files: List<File>, type: ProjectType): CompletableFuture<Map<File, IVersion?>> {
        return supplyAsync {
            val hashes = files.mapNotNull {
                val hash = Utils.getSha1(it)
                if (hash == null) null else hash to it
            }.toMap()
            val loader = when (type) {
                ProjectType.RESOURCE_PACK, ProjectType.AYCY_RESOURCE_PACK -> "minecraft"
                ProjectType.IRIS_SHADER -> "iris"
                ProjectType.OPTIFINE_SHADER -> "optifine"
                else -> error("$type is not supported in updates")
            }
            val data: Map<String, ModrinthVersion> = URL("${API}/version_files/update").postAndGetJson<Map<String, ModrinthVersion>, ModrinthUpdateFormat>(
                ModrinthUpdateFormat(loaders = listOf(loader), hashes = hashes.keys.toList())
            ) ?: error("Failed to fetch updates")
            // Associate with file, and if we already have the latest version, set the result to null
            data.map { hashes[it.key]!! to if (it.key == it.value.getSha1()) null else it.value }.toMap()
        }
    }

    private fun ProjectType.getProjectType(): String? = when (this) {
        ProjectType.RESOURCE_PACK -> "resourcepack"
        ProjectType.AYCY_RESOURCE_PACK -> "resourcepack"
        ProjectType.DATA_PACK -> "mod"
        ProjectType.IRIS_SHADER -> "shader"
        ProjectType.OPTIFINE_SHADER -> "shader"
        else -> null
    }

    override fun getSortOptions(): Map<String, String> = mapOf(
        "relevance" to "resourcify.browse.sort.relevance",
        "downloads" to "resourcify.browse.sort.downloads",
        "follows" to "resourcify.browse.sort.follows",
        "newest" to "resourcify.browse.sort.newest",
        "updated" to "resourcify.browse.sort.updated",
    )
}