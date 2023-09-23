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
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

class MinecraftButton(text: String? = null) : UIContainer() {
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
        (if (isHovered()) highlightedTexture else texture)?.let {
            Utils.drawTexture(
                matrixStack, it,
                this.getLeft().toDouble(),
                this.getTop().toDouble(),
                0.0,
                0.0,
                this.getWidth().toDouble() / 2,
                this.getHeight().toDouble(),
                200.0, 20.0
            )
            Utils.drawTexture(
                matrixStack, it,
                this.getLeft().toDouble() + this.getWidth().toDouble() / 2,
                this.getTop().toDouble(),
                200.0 - this.getWidth().toDouble() / 2,
                0.0,
                this.getWidth().toDouble() / 2,
                this.getHeight().toDouble(),
                200.0, 20.0
            )
        }
        super.draw(matrixStack)
    }

    companion object {
        private var texture: ReleasedDynamicTexture? = null
        private var highlightedTexture: ReleasedDynamicTexture? = null

        fun reloadTexture(resourceManager: ResourceManager) {
            texture = loadTexture("textures/gui/sprites/widget/button.png", resourceManager, texture)
            highlightedTexture =
                loadTexture("textures/gui/sprites/widget/button_highlighted.png", resourceManager, highlightedTexture)
        }

        private fun loadTexture(
            identifier: String,
            resourceManager: ResourceManager,
            texture: ReleasedDynamicTexture?
        ): ReleasedDynamicTexture? {
            val resource = try {
                resourceManager.getResource(Identifier(identifier))
            } catch (e: Exception) {
                return null
            }
            if (resource.isEmpty) return null
            val stream = resource.get().inputStream
            val tex = UGraphics.getTexture(stream)
            tex.uploadTexture()
            texture?.clearGlId()
            return tex
        }
    }
}