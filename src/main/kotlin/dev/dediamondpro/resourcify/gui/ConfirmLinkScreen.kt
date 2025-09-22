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

package dev.dediamondpro.resourcify.gui

import dev.dediamondpro.resourcify.util.toURI
import gg.essential.universal.UDesktop
import gg.essential.universal.UKeyboard
import gg.essential.universal.UScreen
import net.minecraft.client.gui.screens.ConfirmLinkScreen
import net.minecraft.client.gui.screens.Screen

//? if >=1.21.9
import net.minecraft.client.input.KeyEvent


class ConfirmLinkScreen(private val url: String, private val previousScreen: Screen?, trusted: Boolean = false) :
    ConfirmLinkScreen({ result ->
        if (result) UDesktop.browse(url.toURI())
        UScreen.displayScreen(previousScreen)
    }, url, trusted) {

    override fun keyPressed(
        //? if <1.21.9 {
        /*keyCode: Int, scanCode: Int, modifiers: Int
        *///?} else
        keyEvent: KeyEvent
    ): Boolean {
        val key = /*? if <1.21.9 {*/ /*keyCode *//*?} else {*/ keyEvent.key /*?}*/
        if (key == UKeyboard.KEY_ESCAPE) {
            UScreen.displayScreen(previousScreen)
            return true
        }
        //? if <1.21.9 {
        /*return super.keyPressed(keyCode, scanCode, modifiers)
        *///?} else
        return super.keyPressed(keyEvent)
    }
}