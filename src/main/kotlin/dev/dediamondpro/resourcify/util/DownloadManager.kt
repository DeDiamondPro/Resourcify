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

import dev.dediamondpro.resourcify.Constants
import dev.dediamondpro.resourcify.platform.Platform
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Stack
import java.util.concurrent.CompletableFuture

object DownloadManager {
    private val tempFolder = Platform.getFileInGameDir("resourcify-temp")

    @get:Synchronized
    private val queuedDownloads = mutableMapOf<URI, QueuedDownload>()

    @get:Synchronized
    private val downloadsInProgress = mutableMapOf<URI, DownloadData>()

    fun download(
        file: File, sha512: String? = null, uri: URI,
        extract: Boolean = false, callback: (() -> Unit)? = null,
    ) {
        queuedDownloads[uri] = QueuedDownload(file, sha512, extract, callback)
        downloadNext()
    }

    fun getProgress(uri: URI): Float? {
        if (queuedDownloads.containsKey(uri)) return 0f
        if (!downloadsInProgress.containsKey(uri)) return null
        val length = downloadsInProgress[uri]?.length ?: return 0f
        return (downloadsInProgress[uri]?.file?.length()?.toFloat() ?: 0f) / length
    }

    fun cancelDownload(uri: URI) {
        if (queuedDownloads.containsKey(uri)) {
            queuedDownloads.remove(uri)
            return
        }
        if (!downloadsInProgress.containsKey(uri)) return
        downloadsInProgress[uri]?.future?.cancel(true)
        downloadsInProgress[uri]?.file?.delete()
        downloadsInProgress.remove(uri)
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
            val con = url.toURL().setupConnection()
            downloadsInProgress[url]?.length = con.contentLength
            con.getEncodedInputStream().use {
                Files.copy(it!!, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            if (!downloadsInProgress.containsKey(url)) {
                // This means the download has been canceled
                tempFile.delete()
                return@runAsync
            }
            queuedDownload.sha1?.let {
                val hash = Utils.getSha1(tempFile)
                if (hash == it) return@let
                tempFile.delete()
                error("Hash $hash does not match expected hash $it!")
            }
            if (queuedDownload.extract) {
                val targetFolder = queuedDownload.file
                extractWorldZip(tempFile, targetFolder)
            } else {
                Files.move(tempFile.toPath(), queuedDownload.file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            tempFile.delete()
            queuedDownload.callback?.let { it() }
        }.whenComplete { _, throwable ->
            if (throwable != null) {
                Constants.LOGGER.error("Failed to download '$url'", throwable)
                tempFile.delete()
            }
            downloadsInProgress.remove(url)
            downloadNext()
        }, tempFile)
    }

    fun extractWorldZip(zipFile: File, dest: File) {
        // If all content is actually inside another folder inside the zip file, try to find this folder
        // We do this by checking if there is only one folder at the root, and then take this folder
        dest.mkdirs()

        // Use the deprecated constructor since 1.20.1 uses an older version of the commons compress library
        ZipFile(zipFile).use { zip ->
            // For worlds (which is the only thing currently using this) level.dat should be at the root extracted
            // folder, every file not in the same folder or a sub folder of level.dat will be ignored
            var prefixToRemove: String? = null

            // Prefix finding pass
            val entriesToExtract = Stack<ZipArchiveEntry>()
            val entries = zip.entries
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                entriesToExtract.push(entry)

                val fileName = entry.name.substringAfterLast("/")
                if (fileName == "level.dat") {
                    prefixToRemove = entry.name.substringBeforeLast("/")
                    if (prefixToRemove.isEmpty()) {
                        prefixToRemove = null
                    } else {
                        prefixToRemove += "/"
                    }
                    break
                }
            }

            // Extracting pass
            while (entriesToExtract.isNotEmpty() || entries.hasMoreElements()) {
                val entry = if (entriesToExtract.isNotEmpty()) entriesToExtract.pop() else entries.nextElement()

                if (prefixToRemove != null && !entry.name.startsWith(prefixToRemove)) {
                    // Filter out files
                    continue
                }

                val entryFile = resolvePath(entry, dest, prefixToRemove)
                if (entry.isDirectory) {
                    entryFile.mkdirs()
                    continue
                } else {
                    entryFile.parentFile.mkdirs()
                }

                zip.getInputStream(entry).use {
                    Files.copy(it, entryFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }
    }

    private fun resolvePath(entry: ZipArchiveEntry, targetDir: File, prefix: String?): File {
        val entryName = entry.name.let { if (prefix != null) it.removePrefix(prefix) else it }
        val destination = targetDir.resolve(entryName).normalize()
        if (!destination.absolutePath.startsWith(targetDir.normalize().absolutePath)) {
            error("Bad zip entry, ${entry.name} is not in correct directory.")
        }
        return destination
    }
}

private data class QueuedDownload(val file: File, val sha1: String?, val extract: Boolean, val callback: (() -> Unit)?)

private data class DownloadData(val future: CompletableFuture<Void>, val file: File, var length: Int? = null)