/*
 * This file is part of Resourcify
 * Copyright (C) 2026 DeDiamondPro
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

/**
 * Data to contribute to Modrinth's analytics,
 * see https://modrinth.com/news/article/analytics-overhaul/ (bottom of article)
 */
data class ModrinthAnalytics(
    @SerializedName("download_reason") val downloadReason: DownloadReason? = null,
    @SerializedName("game_version") val gameVersion: String? = null,
    @SerializedName("loader") val loader: String? = null,
) {
    enum class DownloadReason {
        @SerializedName("standalone") STANDALONE,
        @SerializedName("dependency") DEPENDENCY,
        @SerializedName("modpack") MODPACK,
        @SerializedName("update") UPDATE,
    }
}