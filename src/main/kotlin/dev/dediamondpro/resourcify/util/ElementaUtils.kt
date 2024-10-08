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
import gg.essential.elementa.components.image.DefaultLoadingImage
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UResolution
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import java.awt.Color

fun UIImage.Companion.ofURL(
    source: String,
    loadingImage: Boolean = true,
    width: Float? = null,
    height: Float? = null,
    fit: ImageURLUtils.Fit = ImageURLUtils.Fit.INSIDE,
    scaleFactor: Float = UResolution.scaleFactor.toFloat(),
    useCache: Boolean = true,
    minFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
    magFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.LINEAR,
): UIImage {
    val url = source.toURL()
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
}