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