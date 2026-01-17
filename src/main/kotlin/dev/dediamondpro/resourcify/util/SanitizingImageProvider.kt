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

object SanitizingImageProvider : ImageProvider<IUIImage> {
    override fun getImage(
        src: String,
        dimensionCallback: Consumer<ImageProvider.Dimension>,
        imageCallback: Consumer<IUIImage>
    ) {
        CompletableFuture.runAsync {
            val transformedUri = ImageURLUtils.getTransformedImageUrl(src.toURI())
            (transformedUri.toURL().setupConnection().apply {
                setRequestProperty("Accept", "image/*")
            }.getEncodedInputStream() ?: error("Failed to load image $src")).use {
                if (UIAnimatedImage.supportsExtension(ImageURLUtils.getExtension(transformedUri))) {
                    val image = UIAnimatedImage.fromGif(it, loadAsync = false, loadingImage = EmptyImage)
                    dimensionCallback.accept(ImageProvider.Dimension(image.imageWidth, image.imageHeight))
                    imageCallback.accept(image)
                } else {
                    ImageIO.createImageInputStream(it).use { imageIn ->
                        val readers = ImageIO.getImageReaders(imageIn)
                        check(readers.hasNext()) { "No image reader found for $src" }
                        val reader = readers.next()
                        try {
                            reader.setInput(imageIn)
                            dimensionCallback.accept(
                                ImageProvider.Dimension(
                                    reader.getWidth(0).toFloat(),
                                    reader.getHeight(0).toFloat()
                                )
                            )
                            imageCallback.accept(
                                UIImageWrapper(
                                    UIImage(
                                        supply { reader.read(0) },
                                        EmptyImage
                                    )
                                )
                            )
                        } finally {
                            reader.dispose()
                        }
                    }
                }
            }
        }
    }
}