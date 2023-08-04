/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
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

import dev.dediamondpro.resourcify.util.Utils
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.USound
import gg.essential.universal.utils.ReleasedDynamicTexture
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation

class MinecraftButton(private val text: String? = null) : UIContainer() {
    init {
        if (text != null) {
            UIText(text).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            } childOf this
        }
        onMouseClick { if (it.mouseButton == 0) USound.playButtonPress() }
    }

    override fun draw(matrixStack: UMatrixStack) {
        texture?.let {
            Utils.drawTexture(
                matrixStack, it,
                this.getLeft().toDouble(),
                this.getTop().toDouble(),
                0.0,
                66.0 + if (isHovered()) 20.0 else 0.0,
                this.getWidth().toDouble() / 2,
                this.getHeight().toDouble()
            )
            Utils.drawTexture(
                matrixStack, it,
                this.getLeft().toDouble() + this.getWidth().toDouble() / 2,
                this.getTop().toDouble(),
                200.0 - this.getWidth().toDouble() / 2,
                66.0 + if (isHovered()) 20.0 else 0.0,
                this.getWidth().toDouble() / 2,
                this.getHeight().toDouble()
            )
        }
        super.draw(matrixStack)
    }

    companion object {
        private var texture: ReleasedDynamicTexture? = null

        fun reloadTexture(resourceManager: IResourceManager) {
            val resource = try {
                resourceManager.getResource(ResourceLocation("textures/gui/widgets.png"))
            } catch (e: Exception) {
                return
            }
            //#if MC >= 11900
            //$$ val stream = resource.get().inputStream
            //#else
            val stream = resource.inputStream
            //#endif
            val tex = UGraphics.getTexture(stream)
            tex.uploadTexture()
            texture?.deleteGlTexture()
            texture = tex
        }
    }
}