/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.CompletableFuture

object DownloadManager {
    private val tempFolder = File("./resourcify-temp")

    @get:Synchronized
    private val queuedDownloads = mutableMapOf<URL, QueuedDownload>()

    @get:Synchronized
    private val downloadsInProgress = mutableMapOf<URL, DownloadData>()

    fun download(
        file: File, sha512: String? = null, url: URL,
        extract: Boolean = false, callback: (() -> Unit)? = null,
    ) {
        queuedDownloads[url] = QueuedDownload(file, sha512, extract, callback)
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
        tempFolder.mkdirs()
        var tempFile = File(tempFolder, queuedDownload.file.name + ".tmp")
        val i = 0
        while (tempFile.exists()) {
            tempFile = File(tempFolder, queuedDownload.file.name + "-$i.tmp")
        }
        downloadsInProgress[url] = DownloadData(runAsync {
            val con = url.setupConnection()
            downloadsInProgress[url]?.length = con.contentLength
            con.getEncodedInputStream().use {
                Files.copy(it!!, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            queuedDownload.sha1?.let {
                val hash = Utils.getSha1(tempFile)
                if (hash == it) return@let
                tempFile.delete()
                error("Hash $hash does not match expected hash $it!")
            }
            if (queuedDownload.extract) {
                val targetFolder = queuedDownload.file
                targetFolder.mkdirs()
                ZipFile(tempFile).use { zip ->
                    // If all content is actually inside another folder inside the zip file, try to find this folder
                    // We do this by checking if there is only one folder at the root, and then take this folder
                    var prefixToRemove: String? = null
                    for (entry in zip.entries) {
                        val firstFolder = entry.name.substringBefore("/", "")
                        // Could be readme file, license file, ...
                        if (firstFolder.isEmpty()) {
                            continue
                        }
                        if (prefixToRemove == null) {
                            prefixToRemove = "$firstFolder/"
                        } else if (prefixToRemove != "$firstFolder/") {
                            prefixToRemove = null
                            break
                        }
                    }

                    zip.entries.asSequence().forEach { entry ->
                        // Remove prefix so when a zip contains a folder which contains the actual files,
                        // this will handle it
                        val entryName =
                            entry.name.let { if (prefixToRemove != null) it.removePrefix(prefixToRemove) else it }
                        val entryFile = File(targetFolder, entryName)
                        if (entryFile.startsWith("..") || entryFile.startsWith("/")) {
                            error("Safety measure tripped for $entryFile, it is trying to go to parent directory")
                        }
                        if (entry.isDirectory) {
                            entryFile.mkdirs()
                            return@forEach
                        } else {
                            entryFile.parentFile.mkdirs()
                        }

                        zip.getInputStream(entry).use {
                            Files.copy(it, entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        }
                    }
                }
            } else {
                Files.move(tempFile.toPath(), queuedDownload.file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            tempFile.delete()
            queuedDownload.callback?.let { it() }
        }.whenComplete { _, throwable ->
            if (throwable != null) {
                println("Download of $url failed:")
                throwable.printStackTrace()
                tempFile.delete()
                return@whenComplete
            }
            downloadsInProgress.remove(url)
            downloadNext()
        }, tempFile)
    }
}

private data class QueuedDownload(val file: File, val sha1: String?, val extract: Boolean, val callback: (() -> Unit)?)

private data class DownloadData(val future: CompletableFuture<Void>, val file: File, var length: Int? = null)