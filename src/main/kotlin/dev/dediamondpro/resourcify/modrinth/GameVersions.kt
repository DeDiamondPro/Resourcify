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

package dev.dediamondpro.resourcify.modrinth

import com.google.gson.annotations.SerializedName
import dev.dediamondpro.resourcify.util.getJson
import dev.dediamondpro.resourcify.util.supplyAsync
import gg.essential.elementa.components.Window
import java.net.URL
import java.util.concurrent.CompletableFuture

data class GameVersions(
    val version: String,
    @SerializedName("version_type") val versionType: String,
    val date: String,
    val major: Boolean
) {
    companion object {
        private val versions: CompletableFuture<List<GameVersions>> by lazy {
            supplyAsync {
                (URL("https://api.modrinth.com/v2/tag/game_version").getJson<List<GameVersions>>(useCache = false)
                    ?: emptyList()).reversed()
            }
        }

        fun getVersionsWhenLoaded(callback: (List<GameVersions>) -> Unit) {
            if (versions.isDone) callback(versions.get() ?: emptyList())
            else versions.whenComplete { versions, _ ->
                Window.enqueueRenderOperation { callback(versions ?: emptyList()) }
            }
        }

        fun getVersions(snapshots: Boolean = false): List<GameVersions> {
            var response = versions.get() ?: emptyList()
            if (!snapshots) response = response.filter { it.versionType == "release" }
            return response
        }

        fun formatVersions(rawVersions: List<String>): String {
            val versions = rawVersions.mapNotNull { raw -> getVersions(true).firstOrNull { raw == it.version } }
            return formatVersionPart(versions)
        }

        private fun formatVersionPart(versions: List<GameVersions>): String {
            if (versions.isEmpty()) return ""
            val allVersions = getVersions(versions[0].versionType != "release")
            val firstIndex = allVersions.indexOf(versions[0])
            return buildString {
                var index = 0
                for ((i, version) in versions.withIndex()) {
                    if (firstIndex + i >= allVersions.size || version != allVersions[firstIndex + i]) break
                    index = i
                }
                when (index) {
                    0 -> append(versions[0].version)
                    1 -> {
                        append(versions[0].version)
                        append(", ")
                        append(versions[1].version)
                    }

                    else -> {
                        append(versions[0].version)
                        append("—")
                        append(versions[index].version)
                    }
                }
                val nextPart = formatVersionPart(versions.subList(index + 1, versions.size))
                if (nextPart.isNotBlank()) {
                    append(", ")
                    append(nextPart)
                }
            }
        }
    }
}
