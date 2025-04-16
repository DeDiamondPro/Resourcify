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

package dev.dediamondpro.resourcify.services.curseforge

import dev.dediamondpro.resourcify.services.*
import dev.dediamondpro.resourcify.util.*
import java.net.URI
import java.util.concurrent.CompletableFuture

data class CurseForgeVersion(
    private val id: Int,
    private val modId: Int,
    private val displayName: String,
    private val fileName: String,
    private val downloadUrl: String?,
    private val hashes: List<Hash>,
    private val releaseType: Int,
    private val gameVersions: List<String>,
    private val downloadCount: Int,
    private val fileDate: String,
    private val dependencies: List<Dependency>,
    val fileFingerprint: Long,
    private val fileLength: Long,
) : IVersion {
    var rootProjectLink: String? = null

    @Transient
    private var changeLogRequest: CompletableFuture<String>? = null

    @Transient
    private var dependenciesRequest: CompletableFuture<List<CurseForgeDependency>>? = null

    override fun getName(): String = displayName
    override fun getVersionNumber(): String? = null
    override fun getProjectId(): String = modId.toString()
    fun hasDownloadUrl(): Boolean = downloadUrl != null
    override fun getDownloadUrl(): URI? = downloadUrl?.toURIOrNull()
    override fun getViewUrl(): URI = "${rootProjectLink ?: error("Root project link not set")}/files/$id".toURI()
    override fun getFileName(): String = fileName
    override fun getSha1(): String = hashes.firstOrNull { it.algo == 1 }?.value ?: ""
    override fun getFileSize(): Long = fileLength

    override fun getChangeLog(): CompletableFuture<String> {
        return changeLogRequest ?: supplyAsync {
            "${CurseForgeService.API}/mods/$modId/files/$id/changelog".toURI()
                .getJson<Changelog>(headers = mapOf("x-api-key" to CurseForgeService.API_KEY))
                ?.data ?: error("Failed to fetch changelog.")
        }.apply { changeLogRequest = this }
    }

    override fun getVersionType(): VersionType = when (releaseType) {
        1 -> VersionType.RELEASE
        2 -> VersionType.BETA
        3 -> VersionType.ALPHA
        else -> VersionType.RELEASE
    }

    override fun getLoaders(): List<String> =
        gameVersions.filter { !MC_VERSION_REGEX.containsMatchIn(it) }.ifEmpty { listOf("Minecraft") }

    override fun getMinecraftVersions(): List<String> {
        return gameVersions.filter { MC_VERSION_REGEX.containsMatchIn(it) }.sortedBy {
            it.split(".").joinToString("") { v ->
                v.replace(Regex("[^0-9]"), "").padStart(2, '0')
            }.toInt()
        }
    }

    override fun getDownloadCount(): Int = downloadCount
    override fun getReleaseDate(): String = fileDate
    override fun hasDependencies(): Boolean =
        dependencies.none { DependencyType.fromCurseForgeId(it.relationType) == null } && dependencies.isNotEmpty()

    override fun getDependencies(): CompletableFuture<List<IDependency>> {
        return (dependenciesRequest ?: supplyAsync {
            val deps = dependencies.filter { DependencyType.fromCurseForgeId(it.relationType) != null }
            val projects: ModsResponse = "${CurseForgeService.API}/mods".toURI()
                .postAndGetJson(
                    GetByIdProperty(deps.map { it.modId }),
                    headers = mapOf("x-api-key" to CurseForgeService.API_KEY)
                ) ?: error("Failed to fetch dependencies.")
            projects.data.map {
                CurseForgeDependency(
                    it,
                    DependencyType.fromCurseForgeId(deps.first { dep -> dep.modId.toString() == it.getId() }.relationType)!!
                )
            }
        }.apply { dependenciesRequest = this }) as CompletableFuture<List<IDependency>>
    }

    data class Hash(val value: String, val algo: Int)

    data class Changelog(val data: String)

    data class Dependency(val modId: Int, val relationType: Int)

    data class GetByIdProperty(val modIds: List<Int>)

    data class ModsResponse(val data: List<CurseForgeProject>)

    data class CurseForgeDependency(override val project: CurseForgeProject, override val type: DependencyType) :
        IDependency


    companion object {
        val MC_VERSION_REGEX = Regex("([0-9]+\\.[0-9]+(\\.[0-9]+)?)")
    }
}
