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

package dev.dediamondpro.resourcify.elements

import dev.dediamondpro.resourcify.util.EmptyImage
import dev.dediamondpro.resourcify.util.supply
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.image.ImageProvider
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft
import net.minecraft.resources.ResourceLocation
import java.awt.Color
import javax.imageio.ImageIO

class McImage(texture: ResourceLocation) : UIComponent(), ImageProvider {
    var backingImage: UIImage? = null

    init {
        val resource = UMinecraft.getMinecraft().resourceManager.getResource(texture)?.orElse(null)
        if (resource != null) {
            backingImage = UIImage(supply {
                resource.open().use {
                    return@use ImageIO.read(it)
                }
            }, EmptyImage, EmptyImage)
        }
    }

    override fun drawImage(
        matrixStack: UMatrixStack,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        color: Color
    ) {
        backingImage?.drawImage(matrixStack, x, y, width, height, color)
    }

    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()
        val color = this.getColor()

        if (color.alpha == 0) {
            return super.draw(matrixStack)
        }

        this.drawImage(matrixStack, x, y, width, height, color)

        super.draw(matrixStack)
    }
}