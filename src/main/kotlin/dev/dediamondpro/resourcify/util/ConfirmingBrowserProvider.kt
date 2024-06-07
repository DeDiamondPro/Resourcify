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

package dev.dediamondpro.resourcify.util

import dev.dediamondpro.minemark.providers.BrowserProvider
import dev.dediamondpro.resourcify.gui.ConfirmLinkScreen
import gg.essential.universal.UScreen
import java.net.URLDecoder

object ConfirmingBrowserProvider : BrowserProvider {
    override fun browse(url: String) {
        var actualUrl = url
        // CurseForge uses a redirect link that needs to be decoded
        if (actualUrl.startsWith("/linkout?remoteUrl=")) {
            actualUrl =
                URLDecoder.decode(URLDecoder.decode(actualUrl.removePrefix("/linkout?remoteUrl="), "UTF-8"), "UTF-8")
        }
        UScreen.displayScreen(ConfirmLinkScreen(actualUrl, UScreen.currentScreen))
    }
}