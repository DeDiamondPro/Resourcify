/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

package dev.dediamondpro.resourcify.gui

import dev.dediamondpro.resourcify.util.NetworkUtil
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft
import gg.essential.universal.UResolution
import net.minecraft.client.gui.screen.Screen
import kotlin.math.floor

//#if MC>=12000
import net.minecraft.client.gui.DrawContext
//#endif

abstract class PaginatedScreen(private val adaptScale: Boolean = true) : WindowScreen(
    version = ElementaVersion.V5,
    //#if MC>=12005
    drawDefaultBackground = true
    //#else
    //$$ drawDefaultBackground = false
    //#endif
) {
    private var defaultScale = -1

    init {
        if (!replacingScreen) {
            currentScreen?.let { backScreens.add(it) }
            forwardScreens.clear()
        }
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        //#if MC<12005
        //#if MC>=12000
        //$$ val client = UMinecraft.getMinecraft()
        //$$ (this as Screen).renderBackgroundTexture(DrawContext(client, client!!.bufferBuilders.entityVertexConsumers))
        //#elseif MC>=11904
        //$$ (this as Screen).renderBackgroundTexture(matrixStack.toMC())
        //#elseif MC>=11600
        //$$ (this as Screen).renderBackgroundTexture(0)
        //#else
        //$$ (this as GuiScreen).drawBackground(0)
        //#endif
        //#endif
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
    }

    override fun onTick() {
        if (!adaptScale) return
        if (defaultScale == -1) defaultScale = UMinecraft.guiScale
        val updatedScale = getGuiScale(defaultScale)
        if (updatedScale != UResolution.scaleFactor.toInt()) {
            newGuiScale = updatedScale
            updateGuiScale()
            UMinecraft.guiScale = updatedScale
            // Cast to fix some remapping issues
            (this as Screen).width = UResolution.scaledWidth
            (this as Screen).height = UResolution.scaledHeight
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

    override fun onMouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int) {
        when (mouseButton) {
            3 -> goBack()
            4 -> goForward()
            else -> super.onMouseClicked(mouseX, mouseY, mouseButton)
        }
    }

    open fun goBack() {
        val backScreen = backScreens.removeLastOrNull()
        if (backScreen is PaginatedScreen) forwardScreens.add(this)
        else cleanUp()
        displayScreen(backScreen)
    }

    open fun goForward() {
        val forwardScreen = forwardScreens.removeLastOrNull() ?: return
        backScreens.add(this)
        displayScreen(forwardScreen)
    }

    fun replaceScreen(screen: () -> Screen) {
        replacingScreen = true
        displayScreen(screen())
        replacingScreen = false
    }

    companion object {
        private var replacingScreen = false
        val backScreens: MutableList<Screen> = mutableListOf()
        val forwardScreens: MutableList<Screen> = mutableListOf()

        fun cleanUp() {
            backScreens.clear()
            forwardScreens.clear()
            NetworkUtil.clearCache()
        }

        private fun getGuiScale(defaultScale: Int): Int {
            val minScale = floor(UResolution.windowWidth / 692f).toInt()
            return minScale.coerceAtLeast(1).coerceAtMost(calculateScaleFactor(defaultScale))
        }

        private fun calculateScaleFactor(guiScale: Int): Int {
            val mc = UMinecraft.getMinecraft()

            //#if MC>=11502
            return mc.window.calculateScaleFactor(guiScale, mc.forcesUnicodeFont())
            //#else
            //$$ // This is not public in legacy versions, so we have to do it ourselves
            //$$ var i = guiScale
            //$$ var scaleFactor = 1
            //$$ if (i == 0) i = 1000
            //$$ while (scaleFactor < i && UResolution.windowWidth / (scaleFactor + 1) >= 320 && UResolution.windowHeight / (scaleFactor + 1) >= 240) {
            //$$     ++scaleFactor
            //$$ }
            //$$ if (mc.isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1) {
            //$$     --scaleFactor
            //$$ }
            //$$ return scaleFactor
            //#endif
        }
    }
}