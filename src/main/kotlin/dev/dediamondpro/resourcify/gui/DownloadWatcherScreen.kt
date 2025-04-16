package dev.dediamondpro.resourcify.gui

import dev.dediamondpro.resourcify.util.Utils
import gg.essential.universal.UDesktop
import gg.essential.universal.UScreen
import net.minecraft.client.gui.screens.ConfirmScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.util.function.Consumer
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

class DownloadWatcherScreen(
    private val url: URI,
    private val extension: String,
    private val size: Long,
    private val sha1: String,
    private val previousScreen: Screen,
    private val fileCallback: Consumer<Path>,
) : ConfirmScreen(
    { result ->
        if (result) {
            UDesktop.browse(url)
        } else {
            UScreen.displayScreen(previousScreen)
        }
    },
    Component.translatable("resourcify.manual_download.title"),
    Component.translatable("resourcify.manual_download.description", downloadFolder.absolutePathString()),
    Component.translatable("resourcify.manual_download.open"),
    Component.translatable("resourcify.manual_download.cancel")
) {
    private val watchService = FileSystems.getDefault().newWatchService()

    init {
        // Also watch modify since file may be partially written on create, and done on modify
        downloadFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY)
    }

    override fun tick() {
        var key = watchService.poll()
        while (key != null) {
            for (event in key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE || event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    val newFile = event.context() as Path
                    val absolutePath = downloadFolder.resolve(newFile)
                    val file = absolutePath.toFile()

                    // Check extension to ignore .part, .crdownload, ...
                    // Then check if the file still exist just in case it was removed
                    // Then check file size since it is a very cheap test, then finally check hash
                    if (file.extension != extension || !file.exists() || file.length() != size || Utils.getSha1(file) != sha1) {
                        continue
                    }

                    fileCallback.accept(absolutePath)
                    watchService.close()
                    super.tick()
                    return
                }
            }

            key.reset()
            key = watchService.poll()
        }

        super.tick()
    }

    override fun onClose() {
        watchService.close()

        super.onClose()
    }

    companion object {
        private val downloadFolder by lazy { Paths.get(System.getProperty("user.home"), "Downloads") }

        fun isUsable(): Boolean {
            return downloadFolder.exists()
        }
    }
}