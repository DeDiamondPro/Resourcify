/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License Version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dediamondpro.resourcify.util

import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.CompletableFuture

object DownloadManager {

    @get:Synchronized
    private val queuedDownloads = mutableMapOf<URL, QueuedDownload>()

    @get:Synchronized
    private val downloadsInProgress = mutableMapOf<URL, DownloadData>()

    fun download(file: File, url: URL, callback: (() -> Unit)? = null) {
        queuedDownloads[url] = QueuedDownload(file, callback)
        downloadNext()
    }

    fun getProgress(url: URL): Float? {
        if (queuedDownloads.containsKey(url)) return 0f
        if (!downloadsInProgress.containsKey(url)) return null
        val length = downloadsInProgress[url]?.length ?: return 0f
        return (downloadsInProgress[url]?.file?.length()?.toFloat() ?: 0f) / length
    }

    fun cancelDownload(url: URL) {
        if (queuedDownloads.containsKey(url)) {
            queuedDownloads.remove(url)
            return
        }
        if (!downloadsInProgress.containsKey(url)) return
        downloadsInProgress[url]?.future?.cancel(true)
        downloadsInProgress[url]?.file?.delete()
        downloadsInProgress.remove(url)
    }

    private fun downloadNext() {
        if (downloadsInProgress.size >= 2) return
        val url = queuedDownloads.keys.firstOrNull() ?: return
        val queuedDownload = queuedDownloads.remove(url) ?: return
        downloadsInProgress[url] = DownloadData(CompletableFuture.runAsync {
            val con = url.setupConnection()
            //while (downloadsInProgress[file] == null) {} // Wait for this to be inserted, if needed
            downloadsInProgress[url]?.length = con.contentLength
            con.getEncodedInputStream().use {
                Files.copy(it!!, queuedDownload.file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            queuedDownload.callback?.let { it() }
        }.whenCompleteAsync { _, throwable ->
            if (throwable != null) {
                println("Download of $url failed:")
                throwable.printStackTrace()
                return@whenCompleteAsync
            }
            downloadsInProgress.remove(url)
            downloadNext()
        }, queuedDownload.file)
    }
}

private data class QueuedDownload(val file: File, val callback: (() -> Unit)?)

private data class DownloadData(val future: CompletableFuture<Void>, val file: File, var length: Int? = null)