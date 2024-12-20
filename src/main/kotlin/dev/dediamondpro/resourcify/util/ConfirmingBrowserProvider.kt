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
import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.gui.ConfirmLinkScreen
import dev.dediamondpro.resourcify.gui.projectpage.ProjectScreen
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.services.ServiceRegistry
import gg.essential.elementa.components.Window
import gg.essential.universal.UScreen
import java.io.File
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
                val data = service.fetchProjectFromUrl(uri) ?: return false
                val type = data.first
                data.second.whenComplete { project, error ->
                    Window.enqueueRenderOperation {
                        if (error != null || project == null) {
                            UScreen.displayScreen(ConfirmLinkScreen(url, UScreen.currentScreen))
                            return@enqueueRenderOperation
                        }
                        // Try to get download folder on best effort basis
                        val downloadFolder = when (type) {
                            screen.type -> screen.downloadFolder
                            ProjectType.RESOURCE_PACK -> File("./resourcepacks")
                            ProjectType.IRIS_SHADER -> File("./shaderpacks")
                            ProjectType.OPTIFINE_SHADER -> File("./shaderpacks")
                            ProjectType.WORLD -> File("./saves")
                            else -> null
                        }
                        UScreen.displayScreen(ProjectScreen(service, project, type, downloadFolder))
                    }
                }
                return true
            }
        }
        return false
    }
}