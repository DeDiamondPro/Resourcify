/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.util

import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.image.DefaultLoadingImage
import java.net.URL
import java.util.concurrent.CompletableFuture

fun UIImage.Companion.ofURL(
    source: String,
    loadingImage: Boolean = true
): UIImage {
    val url = URL(source)
    val image = UIImage(
        CompletableFuture.supplyAsync { url.getImage() },
        loadingImage = if (loadingImage) DefaultLoadingImage else EmptyImage
    )
    if (!loadingImage) image.imageHeight = 0.5625f
    return image
}