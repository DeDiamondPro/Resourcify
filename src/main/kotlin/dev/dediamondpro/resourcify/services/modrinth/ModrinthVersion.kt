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
import dev.dediamondpro.resourcify.services.*
import dev.dediamondpro.resourcify.util.*
import java.net.URI
import java.util.concurrent.CompletableFuture

data class ModrinthVersion(
    private val name: String,
    @SerializedName("version_number") private val versionNumber: String,
    private val files: List<File>,
    private val changelog: String,
    @SerializedName("version_type") private val versionType: String,
    private val loaders: List<String>,
    @SerializedName("game_versions") private val gameVersions: List<String>,
    private val downloads: Int,
    @SerializedName("date_published") private val datePublished: String,
    @SerializedName("id") private val versionId: String,
    @SerializedName("project_id") private val projectId: String,
    private val dependencies: List<Dependency>,
) : IVersion {
    @Transient
    private var dependenciesRequest: CompletableFuture<List<ModrinthDependency>>? = null

    override fun getName(): String = name
    override fun getVersionNumber(): String = versionNumber
    override fun getProjectId(): String = projectId
    fun hasFile() = files.isNotEmpty()
    private fun getPrimaryFile() = files.firstOrNull { it.primary } ?: files.first()
    override fun getDownloadUrl(): URI? = getPrimaryFile().url.toURIOrNull()
    override fun getViewUrl(): URI = "https://modrinth.com/project/$projectId/version/$versionId".toURI()
    override fun getFileName(): String = getPrimaryFile().filename
    override fun getFileSize(): Long = getPrimaryFile().size
    override fun getSha1(): String = getPrimaryFile().hashes.sha1
    override fun getChangeLog(): CompletableFuture<String> = supply { changelog }
    override fun getVersionType(): VersionType = VersionType.fromName(versionType)
    override fun getLoaders(): List<String> = loaders
    override fun getMinecraftVersions(): List<String> = gameVersions
    override fun getDownloadCount(): Int = downloads
    override fun getReleaseDate(): String = datePublished
    override fun hasDependencies(): Boolean = dependencies.any {
        it.projectId != null && DependencyType.fromString(it.dependencyType) != null
    }

    override fun getDependencies(): CompletableFuture<List<IDependency>> {
        return (dependenciesRequest ?: supplyAsync {
            val requiredDependencies = dependencies.filter {
                it.projectId != null && DependencyType.fromString(it.dependencyType) != null
            }
            val idString = requiredDependencies.joinToString(",", "[", "]") { "\"${it.projectId}\"" }
            UriBuilder("${ModrinthService.API}/projects").addParameter("ids", idString)
                .build().getJson<List<FullModrinthProject>>()?.map {
                    ModrinthDependency(
                        it,
                        DependencyType.fromString(requiredDependencies.first { d -> d.projectId == it.getId() }.dependencyType)!!
                    )
                } ?: emptyList()
        }.apply { dependenciesRequest = this }) as CompletableFuture<List<IDependency>>
    }

    data class File(val url: String, val filename: String, val hashes: Hash, val primary: Boolean, val size: Long)

    data class Hash(val sha1: String)

    data class Dependency(
        @SerializedName("project_id") val projectId: String?,
        @SerializedName("dependency_type") val dependencyType: String
    )

    data class ModrinthDependency(
        override val project: FullModrinthProject,
        override val type: DependencyType
    ) : IDependency
}