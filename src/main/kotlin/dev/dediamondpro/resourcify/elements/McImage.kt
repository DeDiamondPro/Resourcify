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

import com.mojang.blaze3d.systems.RenderSystem
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.image.ImageProvider
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.resources.ResourceLocation
import java.awt.Color

class McImage(private val texture: ResourceLocation) : UIComponent(), ImageProvider {
    override fun drawImage(
        matrixStack: UMatrixStack,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        color: Color
    ) {
        RenderSystem.setShaderTexture(0, texture)
        val renderer = UGraphics.getFromTessellator()

        renderer.beginWithDefaultShader(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR)
        renderer.pos(matrixStack, x, y + height, 0.0).tex(0.0, 1.0).color(color).endVertex()
        renderer.pos(matrixStack, x + width, y + height, 0.0).tex(1.0, 1.0).color(color).endVertex()
        renderer.pos(matrixStack, x + width, y, 0.0).tex(1.0, 0.0).color(color).endVertex()
        renderer.pos(matrixStack, x, y, 0.0).tex(0.0, 0.0).color(color).endVertex()
        renderer.drawDirect()
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