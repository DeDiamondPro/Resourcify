/*
 * This file is part of Resourcify
 * Copyright (C) 2025-2026 DeDiamondPro
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

package dev.dediamondpro.resourcify.elements

import dev.dediamondpro.resourcify.util.image.EmptyImage
import dev.dediamondpro.resourcify.util.supply
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.image.ImageProvider
import gg.essential.elementa.components.image.extractMcScale
import gg.essential.elementa.renderer.ElementaExtractor
import gg.essential.universal.UMinecraft
import java.awt.Color
import javax.imageio.ImageIO

//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation
*///?} else
import net.minecraft.resources.Identifier
import kotlin.math.roundToInt

class McImage(
    texture: /*? if <1.21.11 {*/ /*ResourceLocation *//*?} else {*/Identifier /*?}*/
) : UIComponent(), ImageProvider {
    var backingImage: UIImage? = null

    init {
        val resource = UMinecraft.getMinecraft()?.resourceManager?.getResource(texture)?.orElse(null)
        if (resource != null) {
            backingImage = UIImage(supply {
                resource.open().use {
                    return@use ImageIO.read(it)
                }
            }, EmptyImage, EmptyImage)
        }
    }

    override fun extract(
        extractor: ElementaExtractor,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        color: Color
    ) {
        backingImage?.extract(extractor, x, y, width, height, color)
    }

    override fun extractComponent(extractor: ElementaExtractor) {
        val color = this.getColor()
        if (color.alpha == 0) {
            return
        }

        val x = (this.getLeft() * extractor.guiScale).roundToInt()
        val y = (this.getTop() * extractor.guiScale).roundToInt()
        val width = (this.getWidth() * extractor.guiScale).roundToInt()
        val height = (this.getHeight() * extractor.guiScale).roundToInt()
        this.extract(extractor, x, y, width, height, color)
    }
}