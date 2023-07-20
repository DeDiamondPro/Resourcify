/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
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

package dev.dediamondpro.resourcify.updater

import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.modrinth.ApiInfo
import dev.dediamondpro.resourcify.modrinth.ModrinthUpdateFormat
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
        if (!Config.INSTANCE.checkForUpdates) return
        updateCheck = CompletableFuture.supplyAsync { checkForUpdates() }
    }

    fun displayScreenIfNeeded() {
        if (!Config.INSTANCE.checkForUpdates) return
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
                ).removePrefix("file:").split(".jar")[0] + ".jar"
            )
            val checksum = Utils.getSha512(modFile) ?: return null
            //#if FORGE == 1
            val loader = "forge"
            //#elseif FABRIC == 1
            //$$ val loader = "fabric"
            //#endif
            val response: Map<String, Version> = URL("${ApiInfo.API}/version_files/update")
                .postAndGetJson(ModrinthUpdateFormat(listOf(checksum), listOf(loader))) ?: return null
            val version = response[checksum] ?: return null
            val file = version.files.firstOrNull { it.primary } ?: version.files.firstOrNull() ?: return null
            if (checksum == file.hashes.sha512 || Config.INSTANCE.ignoredVersions.contains(file.hashes.sha512)) return null
            return version to file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}