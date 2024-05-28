package dev.dediamondpro.resourcify.services.ads

import gg.essential.universal.ChatColor

object DefaultAdProvider : IAdProvider {
    override fun isAdAvailable(): Boolean = true

    override fun getText(): String =
        "Host your server on ${ChatColor.AQUA}${ChatColor.BOLD}BisectHosting${ChatColor.RESET} and get 25% off your first month using code ${ChatColor.AQUA}${ChatColor.BOLD}DIAMOND${ChatColor.RESET}."

    override fun getImagePath(): String = "/assets/resourcify/bisect-logo.png"

    override fun getUrl(): String = "https://bisecthosting.com/diamond?r=resourcify"
}