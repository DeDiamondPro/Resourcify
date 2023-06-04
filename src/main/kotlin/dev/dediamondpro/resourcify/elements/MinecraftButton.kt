/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.elements

import dev.dediamondpro.resourcify.util.Utils
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft
import gg.essential.universal.USound
import gg.essential.universal.utils.ReleasedDynamicTexture

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
        Utils.drawTexture(
            matrixStack,
            texture,
            this.getLeft().toDouble(),
            this.getTop().toDouble(),
            0.0,
            66.0 + if (isHovered()) 20.0 else 0.0,
            this.getWidth().toDouble() / 2,
            this.getHeight().toDouble()
        )
        Utils.drawTexture(
            matrixStack,
            texture,
            this.getLeft().toDouble() + this.getWidth().toDouble() / 2,
            this.getTop().toDouble(),
            200.0 - this.getWidth().toDouble() / 2,
            66.0 + if (isHovered()) 20.0 else 0.0,
            this.getWidth().toDouble() / 2,
            this.getHeight().toDouble()
        )
        super.draw(matrixStack)
    }

    companion object {
        private lateinit var texture: ReleasedDynamicTexture

        init {
            Window.enqueueRenderOperation {
                val resourceLocation = "/assets/minecraft/textures/gui/widgets.png"
                texture = UGraphics.getTexture(UMinecraft.getMinecraft()::class.java.getResourceAsStream(resourceLocation))
                texture.uploadTexture()
            }
        }
    }
}