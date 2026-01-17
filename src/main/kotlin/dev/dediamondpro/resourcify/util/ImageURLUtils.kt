/*
 * This file is part of Resourcify
 * Copyright (C) 2024-2026 DeDiamondPro
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

package dev.dediamondpro.resourcify.util

import java.net.URI
import javax.imageio.ImageIO

object ImageURLUtils {
    private val urlExtensionRegex: Regex = Regex(".*\\.(\\w+)\$")

    // Hostnames that won't be automatically proxied
    // Partially taken from https://github.com/modrinth/omorphia/blob/2ed06a96fec38b81ab58bdac0c2bb667960ca1c2/lib/helpers/parse.js#L78
    private val allowedHostNames = listOf(
        "imgur.com",
        "i.imgur.com",
        "cdn-raw.modrinth.com",
        "cdn.modrinth.com",
        "staging-cdn-raw.modrinth.com",
        "staging-cdn.modrinth.com",
        "github.com",
        "raw.githubusercontent.com",
        "user-images.githubusercontent.com",
        "avatars.githubusercontent.com",
        "img.shields.io",
        "raster.shields.io",
        "i.postimg.cc",
        "wsrv.nl",
        "cf.way2muchnoise.eu",
        "bstats.org",
        "media.forgecdn.net",
        "static.planetminecraft.com",
    )

    fun getTransformedImageUrl(
        url: URI,
        width: Float? = null,
        height: Float? = null,
        fit: Fit = Fit.INSIDE
    ): URI {
        val canReadType = hasImageReaderFor(url)
        val useProxy = !allowedHostNames.contains(url.host) || !canReadType || width != null || height != null
        return if (!useProxy) {
            if (url.host == "img.shields.io") {
                URI(
                    url.scheme,
                    url.userInfo,
                    "raster.shields.io",
                    url.port,
                    url.path,
                    url.query,
                    url.fragment
                )
            } else {
                url
            }
        } else {
            UriBuilder("https://wsrv.nl/").apply {
                addParameter("url", url.toString())
                if (!canReadType) addParameter("output", "png")
                if (width != null) addParameter("w", width.toString())
                if (height != null) addParameter("h", height.toString())
                if (width != null || height != null) {
                    addParameter("fit", fit.name.lowercase())
                    addParameter("we", "")
                }
            }.build()
        }
    }

    fun getExtension(uri: URI): String {
        val extension = urlExtensionRegex.replace(uri.rawPath, "$1")
        if (extension == uri.rawPath) {
            return ""
        }
        return extension
    }

    private fun hasImageReaderFor(uri: URI): Boolean {
        val extension = getExtension(uri)
        return extension.isEmpty() || ImageIO.getImageReadersBySuffix(extension).hasNext()
    }

    enum class Fit {
        INSIDE,
        OUTSIDE,
        COVER,
        FILL,
        CONTAIN
    }
}