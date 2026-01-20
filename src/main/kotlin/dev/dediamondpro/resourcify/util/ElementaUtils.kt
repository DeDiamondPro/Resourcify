/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2026 DeDiamondPro
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
import dev.dediamondpro.minemark.elementa.elements.MarkdownBlockquoteComponent
import dev.dediamondpro.minemark.elementa.elements.MarkdownCodeBlockComponent
import dev.dediamondpro.minemark.elementa.elements.MarkdownHeadingComponent
import dev.dediamondpro.minemark.elementa.elements.MarkdownHorizontalRuleComponent
import dev.dediamondpro.minemark.elementa.elements.MarkdownListElementComponent
import dev.dediamondpro.minemark.elementa.elements.MarkdownTableCellComponent
import dev.dediamondpro.minemark.elementa.elements.MarkdownTextComponent
import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.elements.Elements
import dev.dediamondpro.resourcify.elements.McImage
import dev.dediamondpro.resourcify.elements.image.IUIImage
import dev.dediamondpro.resourcify.elements.image.UIAnimatedImage
import dev.dediamondpro.resourcify.elements.image.UIImageWrapper
import dev.dediamondpro.resourcify.elements.markdown.ExpandableMarkdownElement
import dev.dediamondpro.resourcify.elements.markdown.ResourcifyMarkdownImageElement
import dev.dediamondpro.resourcify.elements.markdown.SummaryElement
import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.gui.data.Icons
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UResolution
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import java.io.ByteArrayInputStream
import java.net.URI
import java.util.*
import javax.imageio.ImageIO

fun UIImage.Companion.ofURLCustom(
    uri: URI,
    loadingImage: Boolean = true,
    width: Float? = null,
    height: Float? = null,
    fit: ImageURLUtils.Fit = ImageURLUtils.Fit.INSIDE,
    scaleFactor: Float = UResolution.scaleFactor.toFloat(),
    minFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
    magFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
): IUIImage {
    val transformedUri = ImageURLUtils.getTransformedImageUrl(uri, width, height, fit)

    val image = if (UIAnimatedImage.supportsExtension(ImageURLUtils.getExtension(transformedUri))) {
        UIAnimatedImage(
            framesFuture = AnimatedImageCache.get(transformedUri)?.let { supply { it } }
                ?: supplyAsync {
                    AnimatedImageCache.getOrPut(transformedUri, {
                        UIAnimatedImage.provideFrames(
                            transformedUri.toURL().getImageInputStream() ?: error("Failed to setup connection")
                        )
                    })
                },
            loadingImage = if (loadingImage) ElementaUtils.elementaLoadingImage else EmptyImage
        )
    } else {
        UIImageWrapper(
            ImageCache.getOrPut(transformedUri, {
                UIImage(
                    supplyAsync { ImageIO.read(transformedUri.toURL().getImageInputStream()) },
                    loadingImage = if (loadingImage) ElementaUtils.elementaLoadingImage else EmptyImage
                )
            })
        )
    }

    if (!image.isLoaded() && image.imageWidth == 1f && image.imageHeight == 1f) {
        if (!loadingImage && image.imageHeight == 1f) image.imageHeight = 0.5625f
        if (width != null) image.imageWidth = width * scaleFactor
        if (height != null) image.imageHeight = height * scaleFactor
    }

    image.textureMinFilter = minFilter
    image.textureMagFilter = magFilter
    return image
}

fun UIImage.Companion.ofBase64(
    base64Image: String,
    loadSync: Boolean = false,
    loadingImage: Boolean = true,
    minFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
    magFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
): UIImage {
    val imageFuture = if (loadSync) {
        supply { ImageIO.read(ByteArrayInputStream(Base64.getDecoder().decode(base64Image))) }
    } else {
        supplyAsync { ImageIO.read(ByteArrayInputStream(Base64.getDecoder().decode(base64Image))) }
    }
    val image = UIImage(
        imageFuture,
        loadingImage = if (loadingImage) ElementaUtils.elementaLoadingImage else EmptyImage
    )

    image.textureMinFilter = minFilter
    image.textureMagFilter = magFilter
    return image
}

fun UIComponent.isHidden(): Boolean = !parent.children.contains(this)

fun markdown(
    markdown: String,
    style: MarkdownStyle = Colors.MARKDOWN_STYLE
): MineMarkComponent {
    // Create a MineMark component with our own image and browser provider
    return MineMarkComponent(markdown, style, ElementaUtils.defaultMineMarkCore)
}

object ElementaUtils {
    val defaultMineMarkCore: MineMarkCore<MarkdownStyle, UMatrixStack> =
        MineMarkCore.builder<MarkdownStyle, UMatrixStack>()
            .addExtension(StrikethroughExtension.create())
            .addExtension(TablesExtension.create())
            //.addElementaExtensions()
            .setTextElement(::MarkdownTextComponent)
            .addElement(Elements.HEADING, ::MarkdownHeadingComponent)
            .addElement(Elements.IMAGE, ::ResourcifyMarkdownImageElement) // Custom element
            .addElement(Elements.HORIZONTAL_RULE, ::MarkdownHorizontalRuleComponent)
            .addElement(Elements.LIST_ELEMENT, ::MarkdownListElementComponent)
            .addElement(Elements.BLOCKQUOTE, ::MarkdownBlockquoteComponent)
            .addElement(Elements.CODE_BLOCK, ::MarkdownCodeBlockComponent)
            .addElement(Elements.TABLE_CELL, ::MarkdownTableCellComponent)
            // Other elements
            .addElement(ExpandableMarkdownElement.ExpandableElementCreator)
            .addElement(listOf("summary"), ::SummaryElement)
            .build()

    val elementaLoadingImage = McImage(Icons.LOADING)
}