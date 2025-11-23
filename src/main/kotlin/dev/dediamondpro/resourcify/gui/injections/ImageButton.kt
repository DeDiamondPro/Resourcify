/*
 * This file is part of Resourcify
 * Copyright (C) 2024-2025 DeDiamondPro
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

package dev.dediamondpro.resourcify.gui.injections

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

//? if >= 1.21.6
import net.minecraft.client.renderer.RenderPipelines

//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation
import net.minecraft.client.renderer.RenderType
*///?} else
import net.minecraft.resources.Identifier

class ImageButton(
    screen: Screen, private val xFunc: (Int) -> Int, private val yFunc: (Int) -> Int,
    private val image: /*? if <1.21.11 {*/ /*ResourceLocation *//*?} else {*/Identifier /*?}*/,
    onPress: OnPress,
) : Button(
    xFunc.invoke(screen.width), yFunc.invoke(screen.height),
    20, 20, Component.empty(), onPress,
    DEFAULT_NARRATION,
) {

    fun updateLocation(screen: Screen) {
        this.x = xFunc.invoke(screen.width)
        this.y = yFunc.invoke(screen.height)
    }

    override fun /*? if <1.21.11 {*/ /*renderWidget *//*?} else {*/ renderContents /*?}*/ (
        context: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        delta: Float
    ) {
        //? if <1.21.11
        /*super.renderWidget(context, mouseX, mouseY, delta)*/

        //? if <= 1.21.5
        /*RenderSystem.setShaderColor(1f, 1f, 1f, 1f)*/
        //? if >= 1.21.6 {
        //? if >=1.21.11
        renderDefaultSprite(context)
        context.blit(RenderPipelines.GUI_TEXTURED, image, x + 2, y + 2, 0f, 0f, width - 4, height - 4, 16, 16)
        //?} else if >= 1.21.2 {
        /*context.blit(RenderType::guiTextured, image, x + 2, y + 2, 0f, 0f, width - 4, height - 4, 16, 16)
        *///?} else
        /*context.blit(image, x + 2, y + 2, 0f, 0f, width - 4, height - 4, 16, 16)*/
    }
}