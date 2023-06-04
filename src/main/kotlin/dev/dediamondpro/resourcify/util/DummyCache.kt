/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
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