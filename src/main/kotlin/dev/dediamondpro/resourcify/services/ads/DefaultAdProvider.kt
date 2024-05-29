package dev.dediamondpro.resourcify.services.ads

import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.util.localize
import gg.essential.universal.ChatColor

object DefaultAdProvider : IAdProvider {
    override fun isAdAvailable(): Boolean = Config.instance.adsEnabled
    override fun getText(): String = "resourcify.browse.bisect_ad".localize()
    override fun getImagePath(): String = "/assets/resourcify/bisect-logo.png"
    override fun getUrl(): String = "https://bisecthosting.com/diamond?r=resourcify"
}