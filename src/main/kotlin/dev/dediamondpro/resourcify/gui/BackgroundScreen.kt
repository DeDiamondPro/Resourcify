/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UResolution
import kotlin.math.floor
//#if MC>=12000
//$$ import net.minecraft.client.gui.DrawContext
//#endif

abstract class BackgroundScreen(
    version: ElementaVersion,
    enableRepeatKeys: Boolean = true,
    drawDefaultBackground: Boolean = true,
    restoreCurrentGuiOnClose: Boolean = false
) : WindowScreen(version, enableRepeatKeys, drawDefaultBackground, restoreCurrentGuiOnClose, getGuiScale()) {
    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        //#if MC>=12000
        //$$ renderBackgroundTexture(DrawContext(client, client!!.bufferBuilders.entityVertexConsumers))
        //#elseif MC>=11900 && FABRIC==1
        //$$ renderBackgroundTexture(matrixStack.toMC())
        //#elseif MC>=11900 && FORGE==1
        //$$ renderDirtBackground(matrixStack.toMC())
        //#elseif MC>=11600
        //$$ renderDirtBackground(0)
        //#else
        drawBackground(0)
        //#endif
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        if (restoreCurrentGuiOnClose && window.focusedComponent == null && keyCode == UKeyboard.KEY_ESCAPE) {
            restorePreviousScreen()
        } else {
            super.onKeyPressed(keyCode, typedChar, modifiers)
        }
    }

    companion object {
        private fun getGuiScale(): Int {
            val minScale = floor(UResolution.windowWidth / 692f).toInt()
            return minScale.coerceAtLeast(1).coerceAtMost(UResolution.scaleFactor.toInt())
        }
    }
}