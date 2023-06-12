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