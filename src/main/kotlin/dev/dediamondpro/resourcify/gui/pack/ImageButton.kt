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

package dev.dediamondpro.resourcify.gui.pack

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.util.Identifier
import net.minecraft.text.Text
import net.minecraft.client.render.RenderLayer

class ImageButton(
    screen: Screen, private val xFunc: (Int) -> Int, private val yFunc: (Int) -> Int,
    private val image: Identifier,
    onPress: PressAction,
) : ButtonWidget(
    xFunc.invoke(screen.width), yFunc.invoke(screen.height),
    20, 20, Text.of(null as String?), onPress,
    DEFAULT_NARRATION_SUPPLIER,
) {

    fun updateLocation(screen: Screen) {
        this.x = xFunc.invoke(screen.width)
        this.y = yFunc.invoke(screen.height)
    }

    override fun /*? if >=1.21 {*/ renderWidget/*?} else {*//*renderButton*//*?}*/(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super./*? if >=1.21 {*/ renderWidget/*?} else {*//*renderButton*//*?}*/(context, mouseX, mouseY, delta)
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        context.drawTexture(/*? >=1.21.2 {*/ RenderLayer::getGuiTextured, /*?}*/ image, x + 2, y + 2, 0f, 0f, width - 4, height - 4, 16, 16)
    }
}