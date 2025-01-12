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

package dev.dediamondpro.resourcify.services.curseforge

import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.style.HeadingLevelStyleConfig
import dev.dediamondpro.minemark.style.HeadingStyleConfig
import dev.dediamondpro.minemark.style.ImageStyleConfig
import dev.dediamondpro.minemark.style.LinkStyleConfig
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.services.*
import dev.dediamondpro.resourcify.util.*
import org.apache.http.client.utils.URIBuilder
import java.awt.Color
import java.io.File
import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.format.DateTimeFormatter
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
    override fun isProjectTypeSupported(type: ProjectType): Boolean = type.getClassId() != null

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
                val actualCategories = categories.toMutableList()
                if (type == ProjectType.DATA_PACK) {
                    actualCategories.add("5193") // Add data pack category
                }
                addParameter("categoryIds", "[${actualCategories.joinToString(",")}]")
                addParameter("gameVersions", "[${minecraftVersions.joinToString(",") { "\"$it\"" }}]")
            }.build().toURL().getJson<CurseForgeSearchData>(headers = mapOf("x-api-key" to API_KEY)).apply {
                this?.let { // Filter data packs out of resource packs
                    if (type != ProjectType.RESOURCE_PACK) return@let
                    projects = projects.filter { !it.getInternalCategories().any { c -> c.id == 5193 } }
                }
            }
    }

    override fun getMinecraftVersions(): CompletableFuture<Map<String, String>> {
        fetchMinecraftVersions()
        return minecraftVersions?.thenApply {
            it.associate { version -> version.name to version.name }
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

    override fun getCategories(type: ProjectType): CompletableFuture<Map<String, Map<String, String>>> {
        fetchCategories()
        return categories?.thenApply {
            val classId = type.getClassId()
            mapOf(
                "resourcify.categories.categories".localize() to
                        it.filter { category -> // Filter out data pack category in resource packs, we handle this automatically
                            category.classId == classId && category.id != 5193
                        }.sortedBy { category ->
                            if (!category.name.matches(Regex("^[0-9].*"))) "\uFFFF${category.name}"
                            else category.name.replace(Regex("[^0-9]"), "").toInt().toChar().toString()
                        }.associate { category ->
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

    override fun canFetchProjectUrl(uri: URI): Boolean {
        return false
        // return uri.host == "curseforge.com" || uri.host == "legacy.curseforge.com"
    }

    override fun fetchProjectFromUrl(uri: URI): Pair<ProjectType, CompletableFuture<IProject?>>? {
        // I'd like to support this for curseforge, but I can only fetch the project based on project id,
        // which the URL does not contain (thanks curseforge!)
        return null
    }

    private fun ProjectType.getClassId(): Int? = when (this) {
        ProjectType.RESOURCE_PACK -> 12
        ProjectType.AYCY_RESOURCE_PACK -> 12
        // Data pack class id is 6945, but all data packs are actually under resource packs for some reason
        ProjectType.DATA_PACK -> 12
        ProjectType.IRIS_SHADER -> 6552
        ProjectType.OPTIFINE_SHADER -> 6552
        ProjectType.WORLD -> 17
        else -> null
    }

    override fun getProjectsFromIds(ids: List<String>): Map<String, IProject> {
        return URL("$API/mods")
            .postAndGetJson<CurseForgeModsBatchResponse, CurseForgeModsBatch>(
                CurseForgeModsBatch(ids.map { it.toInt() }), headers = mapOf("x-api-key" to API_KEY)
            )?.data?.associateBy { project -> ids.first { it == project.getId() } }
            ?: error("Failed to fetch mods.")

    }

    override fun getUpdates(files: List<File>, type: ProjectType): CompletableFuture<Map<File, IVersion?>> {
        return supplyAsync {
            val hashes = files.associateBy {
                if (it.length() >= 1024 * 1024 * 512) {
                    // If this file is larger than 512MiB, we will not attempt to load it since for cf's hashing
                    // we need to load the entire file in memory
                    return@associateBy null
                }
                val bytes = it.readBytes()
                MurmurHash2.cfHash(bytes, bytes.size)
            }.filterKeys { it != null }
            val mcVersion = Platform.getMcVersion()
            val result = URL("$API/fingerprints/432")
                .postAndGetJson<CurseForgeFingerprintResponse, CurseForgeFingerprint>(
                    CurseForgeFingerprint(hashes.keys.map { it!! }.toList()),
                    headers = mapOf("x-api-key" to API_KEY)
                )?.data?.exactMatches?.filter {
                    hashes.containsKey(it.file.fileFingerprint)
                            // If there is no download url, we can't download this project so we ignore it
                            && it.file.hasDownloadUrl()
                } ?: error("Failed to fetch updates")

            val fileFutures: MutableMap<CurseForgeFingerprintMatch, CompletableFuture<CurseForgeVersion?>> =
                mutableMapOf()
            for (match in result) {
                val fileCandidate = match.latestFiles.sortedByDescending {
                    Instant.from(DateTimeFormatter.ISO_INSTANT.parse(it.getReleaseDate()))
                }.firstOrNull { file ->
                    file.getMinecraftVersions().contains(mcVersion)
                }
                if (fileCandidate == null && !match.latestFiles.any { it.fileFingerprint == match.file.fileFingerprint }) {
                    fileFutures[match] = supplyAsync {
                        URIBuilder("$API/mods/${match.id}/files")
                            .addParameter("gameVersion", mcVersion)
                            // We only care about the most recent match
                            .addParameter("pageSize", "1")
                            .build().toURL()
                            .getJson<CurseForgeProject.Versions>(headers = mapOf("x-api-key" to API_KEY))
                            ?.data?.sortedByDescending {
                                Instant.from(DateTimeFormatter.ISO_INSTANT.parse(it.getReleaseDate()))
                            }?.firstOrNull() ?: error("Failed to find matching version")
                    }
                } else {
                    // Latest files contains update or file is up to date
                    fileFutures[match] = supply { fileCandidate }
                }
            }

            val updates: MutableMap<File, IVersion?> = mutableMapOf()
            for ((match, future) in fileFutures) {
                try {
                    val file = future.get()
                    if (file?.hasDownloadUrl() == false) {
                        continue
                    }
                    updates[hashes[match.file.fileFingerprint]!!] = file.let {
                        // If the file is up to date, return null
                        if (match.file.fileFingerprint == it?.fileFingerprint) {
                            null
                        } else {
                            it
                        }
                    }
                } catch (_: Exception) {
                }
            }

            return@supplyAsync updates
        }
    }

    override fun getSortOptions(): Map<String, String> = mapOf(
        "1" to "resourcify.browse.sort.relevance",
        "6" to "resourcify.browse.sort.downloads",
        "11" to "resourcify.browse.sort.newest",
        "3" to "resourcify.browse.sort.updated",
    )

    override fun getMarkdownStyle(): MarkdownStyle {
        return MarkdownStyle(
            imageStyle = ImageStyleConfig(SanitizingImageProvider),
            linkStyle = LinkStyleConfig(Color(65, 105, 225), ConfirmingBrowserProvider),
            headerStyle = HeadingStyleConfig(
                HeadingLevelStyleConfig(2f, 12f), // h1
                HeadingLevelStyleConfig(1.66f, 10f), // h2
                HeadingLevelStyleConfig(1.33f, 8f), // h3
                HeadingLevelStyleConfig(1f, 4f), // h4
                HeadingLevelStyleConfig(1f, 4f), // h5
                HeadingLevelStyleConfig(1f, 4f) // h6
            ),
        )
    }
}