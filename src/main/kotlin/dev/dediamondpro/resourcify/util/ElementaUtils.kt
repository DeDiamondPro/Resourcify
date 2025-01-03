/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

import dev.dediamondpro.minemark.MineMarkCore
import dev.dediamondpro.minemark.elementa.MineMarkComponent
import dev.dediamondpro.minemark.elementa.addElementaExtensions
import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.style.ImageStyleConfig
import dev.dediamondpro.minemark.style.LinkStyleConfig
import dev.dediamondpro.resourcify.elements.markdown.ExpandableMarkdownElement
import dev.dediamondpro.resourcify.elements.markdown.SummaryElement
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.image.ImageProvider
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UResolution
import gg.essential.universal.utils.ReleasedDynamicTexture
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import java.awt.Color
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.min

fun UIImage.Companion.ofURLCustom(
    url: URL,
    loadingImage: Boolean = true,
    width: Float? = null,
    height: Float? = null,
    fit: ImageURLUtils.Fit = ImageURLUtils.Fit.INSIDE,
    scaleFactor: Float = UResolution.scaleFactor.toFloat(),
    useCache: Boolean = true,
    minFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
    magFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
): UIImage {
    val image = UIImage(
        url.getImageAsync(
            useCache = useCache,
            width = width?.times(scaleFactor),
            height = height?.times(scaleFactor),
            fit = fit
        ),
        loadingImage = if (loadingImage) ElementaUtils.ElementLoadingImage else EmptyImage
    )
    if (!loadingImage) image.imageHeight = 0.5625f
    if (width != null) image.imageWidth = width * scaleFactor
    if (height != null) image.imageHeight = height * scaleFactor
    image.textureMinFilter = minFilter
    image.textureMagFilter = magFilter
    return image
}

fun UIImage.Companion.ofResourceCustom(
    path: String,
    loadSync: Boolean = false,
    loadingImage: Boolean = true,
    minFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
    magFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
): UIImage {
    val imageFuture = if (loadSync) {
        supply { ImageIO.read(this::class.java.getResourceAsStream(path)) }
    } else {
        supplyAsync { ImageIO.read(this::class.java.getResourceAsStream(path)) }
    }
    val image = UIImage(
        imageFuture,
        loadingImage = if (loadingImage) ElementaUtils.ElementLoadingImage else EmptyImage
    )
    if (!loadingImage) image.imageHeight = 0.5625f
    image.textureMinFilter = minFilter
    image.textureMagFilter = magFilter
    return image
}

fun UIComponent.isHidden(): Boolean = !parent.children.contains(this)

fun markdown(
    markdown: String,
    style: MarkdownStyle = ElementaUtils.defaultMarkdownStyle
): MineMarkComponent {
    // Create a MineMark component with our own image and browser provider
    return MineMarkComponent(markdown, style, ElementaUtils.defaultMineMarkCore)
}

object ElementaUtils {
    val defaultMineMarkCore: MineMarkCore<MarkdownStyle, UMatrixStack> =
        MineMarkCore.builder<MarkdownStyle, UMatrixStack>()
            .addExtension(StrikethroughExtension.create())
            .addExtension(TablesExtension.create())
            .addElementaExtensions()
            .addElement(ExpandableMarkdownElement.ExpandableElementCreator)
            .addElement(listOf("summary"), ::SummaryElement)
            .build()

    val defaultMarkdownStyle: MarkdownStyle = MarkdownStyle(
        imageStyle = ImageStyleConfig(SanitizingImageProvider), linkStyle = LinkStyleConfig(
            Color(65, 105, 225), ConfirmingBrowserProvider
        )
    )

    object ElementLoadingImage : ImageProvider {
        private var image: BufferedImage? =
            ImageIO.read(this.javaClass.getResourceAsStream("/assets/resourcify/elementa-loading.png"))
        private lateinit var texture: ReleasedDynamicTexture

        override fun drawImage(
            matrixStack: UMatrixStack,
            x: Double,
            y: Double,
            width: Double,
            height: Double,
            color: Color
        ) {
            if (!::texture.isInitialized) {
                texture = UGraphics.getTexture(image)
                image = null
            }

            // Get square size
            val size = min(width, height)
            val centerX = x + width / 2.0 - size / 2.0
            val centerY = y + height / 2.0 - size / 2.0

            Utils.drawTexture(
                matrixStack, texture,
                centerX, centerY, 0.0, 0.0,
                size, size, size, size
            )
        }
    }
}