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

package dev.dediamondpro.resourcify.services.ads

import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.util.*
import java.util.concurrent.CompletableFuture

object DefaultAdProvider : IAdProvider {
    private var ads: CompletableFuture<List<RemoteAd>>? = null

    override fun isAdAvailable(): Boolean = Config.instance.adsEnabled

    override fun get(): CompletableFuture<IAdProvider.IAd?> {
        fetchAds()
        return ads?.thenApply { it.randomOrNull() } ?: supply { null }
    }

    private fun fetchAds() {
        if (ads != null && ads?.isCompletedExceptionally == false) return
        ads = supplyAsync {
            "https://api.dediamondpro.dev/resourcify/ads".toURI()
                .getJson<List<RemoteAd>>(useCache = false)
                ?: error("Failed to fetch ads.")
        }
    }

    class RemoteAd : IAdProvider.IAd {
        private val text: String = ""
        private val translate: Boolean? = null
        private val icon: String? = null
        private val url: String = ""

        override fun getText(): String {
            if (translate == true) {
                return text.localize()
            }
            return text
        }

        override fun getImageBase64(): String? {
            return icon
        }

        override fun getUrl(): String {
            return url
        }
    }
}