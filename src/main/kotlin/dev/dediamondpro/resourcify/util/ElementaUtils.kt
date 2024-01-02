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

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.image.DefaultLoadingImage
import gg.essential.universal.UResolution
import java.net.URL

fun UIImage.Companion.ofURL(
    source: String,
    loadingImage: Boolean = true,
    width: Float? = null,
    height: Float? = null,
    fit: ImageURLUtils.Fit = ImageURLUtils.Fit.INSIDE,
    scaleFactor: Float = UResolution.scaleFactor.toFloat(),
    useCache: Boolean = true
): UIImage {
    val url = URL(source)
    val image = UIImage(
        url.getImageAsync(
            useCache = useCache,
            width = width?.times(scaleFactor),
            height = height?.times(scaleFactor),
            fit = fit
        ),
        loadingImage = if (loadingImage) DefaultLoadingImage else EmptyImage
    )
    if (!loadingImage) image.imageHeight = 0.5625f
    if (width != null) image.imageWidth = width * scaleFactor
    if (height != null) image.imageHeight = height * scaleFactor
    return image
}

fun UIComponent.isHidden(): Boolean = !parent.children.contains(this)