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

package dev.dediamondpro.resourcify.gui

import dev.dediamondpro.resourcify.util.toURI
import gg.essential.universal.UDesktop
import gg.essential.universal.UKeyboard
import gg.essential.universal.UScreen
import net.minecraft.client.gui.GuiScreen

//#if MC<11600
import net.minecraft.client.gui.GuiConfirmOpenLink
//#else
//$$ import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen
//#endif


class ConfirmLinkScreen(private val url: String, private val previousScreen: GuiScreen?, trusted: Boolean = false):
    //#if MC<11600
    GuiConfirmOpenLink({ result, _ ->
    //#else
    //$$ ConfirmOpenLinkScreen({ result ->
    //#endif
        if (result) UDesktop.browse(url.toURI())
        UScreen.displayScreen(previousScreen)
    }, url,
        //#if MC<11600
        0,
        //#endif
        trusted
    ) {

    override fun
            //#if MC<11600
            keyTyped(typedChar: Char, keyCode: Int)
            //#else
            //$$ keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean
            //#endif
    {
        if (keyCode == UKeyboard.KEY_ESCAPE) {
            UScreen.displayScreen(previousScreen)
            //#if MC>=11600
            //$$ return true
            //#endif
        } else {
            //#if MC<11600
            super.keyTyped(typedChar, keyCode)
            //#else
            //$$ return super.keyPressed(keyCode, scanCode, modifiers)
            //#endif
        }
    }
}