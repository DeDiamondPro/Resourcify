/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.updater

import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.modrinth.Version
import dev.dediamondpro.resourcify.modrinth.VersionFile
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.util.Utils
import dev.dediamondpro.resourcify.util.postAndGetJson
import gg.essential.universal.UScreen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

object UpdateChecker {
    private var updateCheck: CompletableFuture<Pair<Version, VersionFile>?>? = null

    fun startUpdateCheck() {
        updateCheck = CompletableFuture.supplyAsync { checkForUpdates() }
    }

    fun displayScreenIfNeeded() {
        updateCheck?.let {
            if (it.isDone) {
                val data = it.get()
                if (data != null) {
                    val (version, file) = data
                    UScreen.displayScreen(UpdateGui(version, file))
                }
            } else {
                it.cancel(true)
            }
            updateCheck = null
        }
    }

    private fun checkForUpdates(): Pair<Version, VersionFile>? {
        try {
            val modFile = File(
                URLDecoder.decode(
                    this::class.java.protectionDomain.codeSource.location.file,
                    StandardCharsets.UTF_8.name()
                )
            )
            val checksum = Utils.getSha1(modFile) ?: return null
            val response: Map<String, Version> = URL("https://api.modrinth.com/v2/version_files/update")
                .postAndGetJson(UpdateFormat(listOf(checksum))) ?: return null
            val version = response[checksum] ?: return null
            val file = version.files.firstOrNull { it.primary } ?: version.files.firstOrNull() ?: return null
            if (checksum == file.hashes.sha1 || Config.INSTANCE.ignoredVersions.contains(file.hashes.sha1)) return null
            return version to file
        } catch (_: Exception) {
            return null
        }
    }

    @Serializable
    data class UpdateFormat(
        val hashes: List<String>,
        val algorithm: String = "sha1",
        val loaders: List<String> = listOf("minecraft"),
        @SerialName("game_versions") val gameVersions: List<String> = listOf(Platform.getMcVersion())
    )
}