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
import dev.dediamondpro.resourcify.Constants
import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.gui.ConfirmLinkScreen
import dev.dediamondpro.resourcify.gui.projectpage.ProjectScreen
import dev.dediamondpro.resourcify.platform.TickHandler
import dev.dediamondpro.resourcify.services.ServiceRegistry
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

        val screen = UScreen.currentScreen
        if (Config.instance.openLinkInResourcify && screen is ProjectScreen && tryCreateScreen(actualUrl, screen)) {
            return // We have opened this in a ProjectScreen
        }

        UScreen.displayScreen(ConfirmLinkScreen(actualUrl, UScreen.currentScreen))
    }

    private fun tryCreateScreen(url: String, screen: ProjectScreen): Boolean {
        val uri = url.toURIOrNull() ?: return false
        for (service in ServiceRegistry.getAllServices()) {
            if (service.canFetchProjectUrl(uri)) {
                val future = service.fetchProjectFromUrl(uri) ?: return false
                future.whenComplete { project, error ->
                    TickHandler.runAtNextTick {
                        if (error != null || project == null) {
                            Constants.LOGGER.warn(
                                "Failed to fetch project for \"$url\" from source \"${service.getName()}\"",
                                error
                            )
                            UScreen.displayScreen(ConfirmLinkScreen(url, UScreen.currentScreen))
                            return@runAtNextTick
                        }

                        val type = project.getType()
                        val downloadFolder = type.getDirectoryFromCurrent(screen.type, screen.downloadFolder)
                        UScreen.displayScreen(ProjectScreen(service, project, type, downloadFolder))
                    }
                }
                return true
            }
        }
        return false
    }
}