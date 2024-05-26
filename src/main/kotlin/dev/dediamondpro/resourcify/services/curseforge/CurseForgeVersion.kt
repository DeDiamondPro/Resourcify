package dev.dediamondpro.resourcify.services.curseforge

import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.services.VersionType
import dev.dediamondpro.resourcify.util.encodeUrl
import dev.dediamondpro.resourcify.util.getJson
import dev.dediamondpro.resourcify.util.supplyAsync
import java.net.URL
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
    private val fileDate: String
) : IVersion {
    @Transient
    private var changeLogRequest: CompletableFuture<String>? = null

    override fun getName(): String = displayName
    override fun getVersionNumber(): String? = null
    fun hasDownloadUrl(): Boolean = downloadUrl != null
    override fun getDownloadUrl(): String = downloadUrl ?: error("No download URL.")
    override fun getFileName(): String = fileName
    override fun getSha1(): String = hashes.firstOrNull { it.algo == 1 }?.value ?: ""

    override fun getChangeLog(): CompletableFuture<String> {
        return changeLogRequest ?: supplyAsync {
            URL("${CurseForgeService.API}/mods/$modId/files/$id/changelog")
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

    data class Hash(val value: String, val algo: Int)

    data class Changelog(val data: String)

    companion object {
        val MC_VERSION_REGEX = Regex("([0-9]+\\.[0-9]+(\\.[0-9]+)?)")
    }
}
