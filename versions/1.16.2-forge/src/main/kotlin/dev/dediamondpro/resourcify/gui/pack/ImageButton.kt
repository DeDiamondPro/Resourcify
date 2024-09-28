/*
 * This file is part of Resourcify
 * Copyright (C) 2024 DeDiamondPro
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

package dev.dediamondpro.resourcify.gui.pack

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent

//#if MC >= 11800
//$$ import net.minecraft.client.render.GameRenderer
//#endif

class ImageButton(
    screen: Screen, private val xFunc: (Int) -> Int, private val yFunc: (Int) -> Int,
    private val image: ResourceLocation,
    onPress: IPressable,
) : Button(
    xFunc.invoke(screen.width), yFunc.invoke(screen.height),
    20, 20, ITextComponent.getTextComponentOrEmpty(null as String?), onPress,
    //#if MC >= 11904
    //$$ DEFAULT_NARRATION_SUPPLIER,
    //#endif
) {

    fun updateLocation(screen: Screen) {
        this.x = xFunc.invoke(screen.width)
        this.y = yFunc.invoke(screen.height)
    }

    override fun renderButton(
        //#if MC < 12000
        context: MatrixStack,
        //#else
        //$$ context: DrawContext,
        //#endif
        mouseX: Int, mouseY: Int, delta: Float
    ) {
        super.renderButton(context, mouseX, mouseY, delta)
        //#if MC < 12000
        //#if MC < 11800
        UMinecraft.getMinecraft().textureManager.bindTexture(image)
        RenderSystem.color4f(1f, 1f, 1f, 1f)
        //#else
        //$$ RenderSystem.setShader(GameRenderer::getPositionTexShader)
        //$$ RenderSystem.setShaderTexture(0, image)
        //$$ RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        //#endif
        AbstractGui.blit(context, x + 2, y + 2, 0f, 0f, width - 4, height - 4, 16, 16)
        //#else
        //$$ RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        //$$ context.drawTexture(image, x + 2, y + 2, 0f, 0f, width - 4, height - 4, 16, 16)
        //#endif
    }
}