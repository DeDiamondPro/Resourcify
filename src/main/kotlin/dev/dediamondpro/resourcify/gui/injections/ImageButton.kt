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
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class ImageButton(
    screen: Screen, private val xFunc: (Int) -> Int, private val yFunc: (Int) -> Int,
    private val image: ResourceLocation,
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

    override fun renderWidget(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderWidget(context, mouseX, mouseY, delta)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        context.blit(/*? >=1.21.2 {*/ RenderType::guiTextured, /*?}*/ image, x + 2, y + 2, 0f, 0f, width - 4, height - 4, 16, 16)
    }
}