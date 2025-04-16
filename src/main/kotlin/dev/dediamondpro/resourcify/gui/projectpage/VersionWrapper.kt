package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.world.WorldDownloadingScreen
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.util.DownloadManager
import dev.dediamondpro.resourcify.util.Utils
import dev.dediamondpro.resourcify.util.localize
import gg.essential.elementa.components.UIText
import gg.essential.universal.ChatColor
import gg.essential.universal.UScreen.Companion.displayScreen
import java.io.File

class VersionWrapper(private val version: IVersion, private val type: ProjectType, var installed: Boolean) {
    fun get(): IVersion = version

    fun download(
        downloadFolder: File,
        text: UIText?,
        parent: PaginatedScreen,
        installText: String = "${ChatColor.BOLD}${localize("resourcify.version.installing")}"
    ) {
        if (installed) {
            return
        }

        val url = version.getDownloadUrl() ?: return
        if (DownloadManager.getProgress(url) == null) {
            text?.setText("${ChatColor.BOLD}${localize("resourcify.version.installing")}")
            var fileName = version.getFileName()
            if (type.shouldExtract) {
                fileName = fileName.removeSuffix(".zip")
            }
            var file = File(downloadFolder, fileName)
            if (file.exists()) {
                file = File(downloadFolder, Utils.incrementFileName(version.getFileName()))
            }
            DownloadManager.download(file, version.getSha1(), url, type.shouldExtract) {
                text?.setText("${ChatColor.BOLD}${localize("resourcify.version.installed")}")
                installed = true
            }
            if (type == ProjectType.WORLD) {
                displayScreen(WorldDownloadingScreen(parent, file, url))
            }
        } else {
            DownloadManager.cancelDownload(url)
            text?.setText(installText)
        }
    }
}