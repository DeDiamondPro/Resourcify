/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License Version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dediamondpro.resourcify.util

import gg.essential.elementa.components.image.ImageCache
import java.awt.image.BufferedImage
import java.net.URL

/**
 * Cache used to make sure MarkdownComponent fetches images through our cache layer
 */
object DummyCache : ImageCache {
    override fun get(url: URL): BufferedImage {
        val finalUrl = if (url.host == "img.shields.io") URL(
            url.toExternalForm().replace("https://img.shields.io", "https://raster.shields.io")
        ) else url
        return finalUrl.getImage() ?: error("Failed to fetch $url, don't retry")
    }

    override fun set(url: URL, image: BufferedImage) {} // Should never get called
}