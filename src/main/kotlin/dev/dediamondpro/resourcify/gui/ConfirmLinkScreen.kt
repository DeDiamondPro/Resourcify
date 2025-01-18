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
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ConfirmLinkScreen


class ConfirmLinkScreen(private val url: String, private val previousScreen: Screen?, trusted: Boolean = false) :
    ConfirmLinkScreen({ result ->
        if (result) UDesktop.browse(url.toURI())
        UScreen.displayScreen(previousScreen)
    }, url, trusted) {

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == UKeyboard.KEY_ESCAPE) {
            UScreen.displayScreen(previousScreen)
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}