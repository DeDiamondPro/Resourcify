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
import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent

class ImageButton(
    x: Int, y: Int,
    private val image: ResourceLocation,
    onPress: IPressable,
) : Button(
    x, y, 20, 20, ITextComponent.getTextComponentOrEmpty(null as String?), onPress,
    //#if MC >= 11904
    //$$ DEFAULT_NARRATION_SUPPLIER,
    //#endif
) {

    override fun renderButton(
        //#if MC < 12000
        context: MatrixStack,
        //#else
        //$$ context: DrawContext,
        //#endif
        mouseX: Int, mouseY: Int, delta: Float) {
        super.renderButton(context, mouseX, mouseY, delta)
        UMinecraft.getMinecraft().textureManager.bindTexture(image)
        //#if MC < 12000
        AbstractGui.blit(context, x + 2, y + 2, 0f, 0f, width - 4, height - 4, 16, 16)
        //#else
        //$$ context.drawTexture(image, x + 2, y + 2, 0f, 0f, width - 4, height - 4, 16, 16)
        //#endif
    }
}