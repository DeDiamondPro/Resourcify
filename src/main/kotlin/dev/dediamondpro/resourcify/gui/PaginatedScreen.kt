/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.universal.*
import net.minecraft.client.gui.GuiScreen
import kotlin.math.floor

//#if MC>=12000
//$$ import net.minecraft.client.gui.DrawContext
//#endif

abstract class PaginatedScreen : WindowScreen(version = ElementaVersion.V2, drawDefaultBackground = false) {
    private var defaultScale = -1

    init {
        currentScreen?.let { backScreens.add(it) }
        forwardScreens.clear()
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        //#if MC>=12000
        //$$ renderBackgroundTexture(DrawContext(client, client!!.bufferBuilders.entityVertexConsumers))
        //#elseif MC>=11904
        //$$ renderBackgroundTexture(matrixStack.toMC())
        //#elseif MC>=11600
        //$$ renderDirtBackground(0)
        //#else
        drawBackground(0)
        //#endif
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
    }

    override fun onTick() {
        if (defaultScale == -1) defaultScale = UMinecraft.guiScale
        val updatedScale = getGuiScale(defaultScale)
        if (updatedScale != newGuiScale) {
            newGuiScale = updatedScale
            updateGuiScale()
        }
        super.onTick()
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        if (window.focusedComponent == null && keyCode == UKeyboard.KEY_ESCAPE) {
            goBack()
        } else {
            super.onKeyPressed(keyCode, typedChar, modifiers)
        }
    }

    fun goBack() {
        val backScreen = backScreens.removeLastOrNull()
        if (backScreen is PaginatedScreen) forwardScreens.add(this)
        else cleanUp()
        displayScreen(backScreen)
    }

    fun goForward() {
        val forwardScreen = forwardScreens.removeLastOrNull() ?: return
        backScreens.add(this)
        displayScreen(forwardScreen)
    }

    companion object {
        val backScreens: MutableList<GuiScreen> = mutableListOf()
        val forwardScreens: MutableList<GuiScreen> = mutableListOf()

        fun cleanUp() {
            backScreens.clear()
            forwardScreens.clear()
        }

        private fun getGuiScale(defaultScale: Int): Int {
            val minScale = floor(UResolution.windowWidth / 692f).toInt()
            return minScale.coerceAtLeast(1).coerceAtMost(calculateScaleFactor(defaultScale))
        }

        private fun calculateScaleFactor(guiScale: Int): Int {
            val mc = UMinecraft.getMinecraft()

            //#if MC>=11502
            //$$ return mc.mainWindow.calcGuiScale(guiScale, mc.forceUnicodeFont)
            //#else
            // This is not public in legacy versions, so we have to do it ourselves
            var i = guiScale
            var scaleFactor = 1
            if (i == 0) i = 1000
            while (scaleFactor < i && UResolution.windowWidth / (scaleFactor + 1) >= 320 && UResolution.windowHeight / (scaleFactor + 1) >= 240) {
                ++scaleFactor
            }
            if (mc.isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1) {
                --scaleFactor
            }
            return scaleFactor
            //#endif
        }
    }
}