package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.resourcify.Constants
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
import gg.essential.universal.USound
import net.minecraft.sounds.SoundEvents
import java.io.File
import java.nio.file.Files

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
                    // Play pling sound to signal the file has been detected
                    USound.playSoundStatic(SoundEvents.NOTE_BLOCK_PLING, 1.0f, 1.0f)

                    var file: File
                    try {
                        if (type.shouldExtract) {
                            file = File(downloadFolder, version.getFileName().removeSuffix(".zip"))
                            while (file.exists()) {
                                file = File(downloadFolder, Utils.incrementFileName(file.name))
                            }
                            val sourceFile = it.toFile()
                            DownloadManager.extractZip(sourceFile, file)
                            if (sourceFile.canWrite()) {
                                sourceFile.delete()
                                Constants.LOGGER.warn("Could not delete world file '$it'.")
                            }
                        } else {
                            file = File(downloadFolder, version.getFileName())
                            while (file.exists()) {
                                file = File(downloadFolder, Utils.incrementFileName(file.name))
                            }

                            val sourceFile = it.toFile()
                            // With prism, we can sometimes be in a read-only situation, so don't try to move the file,
                            // and copy it instead
                            if (sourceFile.canWrite()) {
                                Files.move(it, file.toPath())
                            } else {
                                Files.copy(it, file.toPath())
                                Constants.LOGGER.warn("Could not delete file '$it'.")
                            }
                        }

                        text?.setText("${ChatColor.BOLD}${localize("resourcify.version.installed")}")
                        if (type == ProjectType.WORLD) {
                            WorldDownloadingScreen.openWorld(file.name)
                        } else {
                            UScreen.displayScreen(parent)
                        }
                    } catch (e: Exception) {
                        Constants.LOGGER.error("An error occurred while processing file '$it'", e)

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
            while (file.exists()) {
                file = File(downloadFolder, Utils.incrementFileName(file.name))
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