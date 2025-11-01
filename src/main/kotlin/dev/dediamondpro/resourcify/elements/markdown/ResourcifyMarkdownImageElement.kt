/*
 * This file is part of Resourcify
 * Copyright (C) 2025 DeDiamondPro
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

package dev.dediamondpro.resourcify.elements.markdown

import dev.dediamondpro.minemark.LayoutStyle
import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.elementa.util.EmptyImage
import dev.dediamondpro.minemark.elements.Element
import dev.dediamondpro.minemark.elements.impl.ImageElement
import gg.essential.elementa.components.UIImage
import gg.essential.universal.UMatrixStack
import org.xml.sax.Attributes
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture


// Custom markdown image element so we can draw images using linear interpolation instead of nearest
// TODO: this should be merged into MineMark at some point
class ResourcifyMarkdownImageElement(
    style: MarkdownStyle,
    layoutStyle: LayoutStyle,
    parent: Element<MarkdownStyle, UMatrixStack>?,
    qName: String, attributes: Attributes?
) : ImageElement<MarkdownStyle, UMatrixStack, BufferedImage>(style, layoutStyle, parent, qName, attributes) {
    private var uiImage: UIImage? = null

    override fun drawImage(
        image: BufferedImage,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        matrixStack: UMatrixStack
    ) {
        if (uiImage == null) {
            uiImage = UIImage(CompletableFuture.supplyAsync { image }, EmptyImage)
            uiImage?.textureMinFilter = UIImage.TextureScalingMode.LINEAR_MIPMAP_LINEAR
            uiImage?.textureMagFilter = UIImage.TextureScalingMode.LINEAR
        }
        uiImage?.drawImage(
            matrixStack,
            x.toDouble(),
            y.toDouble(),
            width.toDouble(),
            height.toDouble(),
            Color.WHITE
        )
    }
}