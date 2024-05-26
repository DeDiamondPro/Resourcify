package dev.dediamondpro.resourcify.services.modrinth

import com.google.gson.annotations.SerializedName
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.services.VersionType
import dev.dediamondpro.resourcify.util.supply
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
) : IVersion {
    override fun getName(): String = name
    override fun getVersionNumber(): String = versionNumber
    fun hasFile() = files.isNotEmpty()
    private fun getPrimaryFile() = files.firstOrNull { it.primary } ?: files.first()
    override fun getDownloadUrl(): String = getPrimaryFile().url
    override fun getFileName(): String = getPrimaryFile().filename
    override fun getSha1(): String = getPrimaryFile().hashes.sha1
    override fun getChangeLog(): CompletableFuture<String> = supply { changelog }
    override fun getVersionType(): VersionType = VersionType.fromName(versionType)
    override fun getLoaders(): List<String> = loaders
    override fun getMinecraftVersions(): List<String> = gameVersions
    override fun getDownloadCount(): Int = downloads
    override fun getReleaseDate(): String = datePublished

    data class File(val url: String, val filename: String, val hashes: Hash, val primary: Boolean)

    data class Hash(val sha1: String)
}