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

import dev.dediamondpro.minemark.providers.ImageProvider
import dev.dediamondpro.resourcify.elements.image.IUIImage
import dev.dediamondpro.resourcify.elements.image.UIAnimatedImage
import dev.dediamondpro.resourcify.elements.image.UIImageWrapper
import gg.essential.elementa.components.UIImage
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import javax.imageio.ImageIO

object CachingImageProvider : ImageProvider<IUIImage> {
    override fun getImage(
        src: String,
        dimensionCallback: Consumer<ImageProvider.Dimension>,
        imageCallback: Consumer<IUIImage>
    ) {
        CompletableFuture.runAsync {
            val transformedUri = ImageURLUtils.getTransformedImageUrl(src.toURI())
            if (UIAnimatedImage.supportsExtension(ImageURLUtils.getExtension(transformedUri))) {
                val frames = AnimatedImageCache.getOrPut(transformedUri) {
                    UIAnimatedImage.provideFrames(
                        transformedUri.toURL().getImageInputStream() ?: error("Failed to setup connection")
                    )
                }
                val image = UIAnimatedImage(supply { frames }, loadingImage = EmptyImage)
                dimensionCallback.accept(ImageProvider.Dimension(image.imageWidth, image.imageHeight))
                imageCallback.accept(image)
            } else {
                val image = UIImageWrapper(
                    ImageCache.getOrPut(transformedUri, {
                        UIImage(
                            supply { ImageIO.read(transformedUri.toURL().getImageInputStream()) },
                            loadingImage =  EmptyImage
                        )
                    })
                )
                dimensionCallback.accept(ImageProvider.Dimension(image.imageWidth, image.imageHeight))
                imageCallback.accept(image)
            }
        }
    }
}