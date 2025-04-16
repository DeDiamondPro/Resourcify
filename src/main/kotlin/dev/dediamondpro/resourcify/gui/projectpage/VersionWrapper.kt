package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.resourcify.gui.ConfirmLinkScreen
import dev.dediamondpro.resourcify.gui.DownloadWatcherScreen
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.world.WorldDownloadingScreen
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.util.DownloadManager
import dev.dediamondpro.resourcify.util.Utils
import dev.dediamondpro.resourcify.util.localize
import gg.essential.elementa.components.UIText
import gg.essential.universal.ChatColor
import gg.essential.universal.UScreen
import java.io.File
import java.nio.file.Files
import kotlin.io.path.deleteIfExists

class VersionWrapper(private val version: IVersion, private val type: ProjectType, var installed: Boolean) {
    fun get(): IVersion = version

    fun download(
        downloadFolder: File,
        text: UIText?,
        parent: PaginatedScreen,
        installText: String = "${ChatColor.BOLD}${localize("resourcify.version.install")}"
    ) {
        if (installed) {
            return
        }

        val url = version.getDownloadUrl()
        if (url == null) {
            // Use zip as extension since every type we support should download zip files
            val screen = if (DownloadWatcherScreen.isUsable()) {
                DownloadWatcherScreen(version.getViewUrl(), "zip", version.getFileSize(), version.getSha1(), parent) {
                    var file: File
                    if (type.shouldExtract) {
                        file = File(downloadFolder, version.getFileName().removeSuffix(".zip"))
                        if (file.exists()) {
                            file = File(downloadFolder, Utils.incrementFileName(version.getFileName()))
                        }
                        DownloadManager.extractZip(it.toFile(), file)
                        it.deleteIfExists()
                    } else {
                        file = File(downloadFolder, version.getFileName())
                        if (file.exists()) {
                            file = File(downloadFolder, Utils.incrementFileName(version.getFileName()))
                        }
                        Files.move(it, file.toPath())
                    }

                    text?.setText("${ChatColor.BOLD}${localize("resourcify.version.installed")}")
                    if (type == ProjectType.WORLD) {
                        WorldDownloadingScreen.openWorld(file.name)
                    } else {
                        UScreen.displayScreen(parent)
                    }
                }
            } else {
                ConfirmLinkScreen(version.getViewUrl().toString(), parent, true)
            }
            UScreen.displayScreen(screen)
            return
        }

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
                UScreen.displayScreen(WorldDownloadingScreen(parent, file, url))
            }
        } else {
            DownloadManager.cancelDownload(url)
            text?.setText(installText)
        }
    }
}