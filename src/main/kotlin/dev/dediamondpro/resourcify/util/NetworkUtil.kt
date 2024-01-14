/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
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

// decodeFromString import is required on older kotlin versions
@file:Suppress("unusedImport")

package dev.dediamondpro.resourcify.util

import dev.dediamondpro.resourcify.ModInfo
import gg.essential.universal.UMinecraft
import java.awt.image.BufferedImage
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.*
import java.util.zip.DeflaterInputStream
import java.util.zip.GZIPInputStream
import javax.imageio.ImageIO
import javax.net.ssl.HttpsURLConnection

object NetworkUtil {
    private const val MAX_CACHE_SIZE = 100_000_000
    private val cache = ConcurrentHashMap<URL, CacheObject>()
    private val currentlyFetching = ConcurrentHashMap<URL, CompletableFuture<ByteArray?>>()

    // Use a custom, larger, thread-pool to increase fetching speed of images and avoid flooding the main pool
    val ImageThreadPool = Executors.newCachedThreadPool()

    fun getOrFetch(url: URL, executor: Executor = ForkJoinPool.commonPool()): ByteArray? {
        return cache[url]?.getBytes() ?: currentlyFetching[url]?.get() ?: startFetch(url, executor).get()
    }

    fun getOrFetchAsync(url: URL, executor: Executor = ForkJoinPool.commonPool()): CompletableFuture<ByteArray?> {
        return cache[url]?.getBytes()?.let {
            CompletableFuture.supplyAsync({ it }, executor)
        } ?: currentlyFetching[url] ?: startFetch(url, executor)
    }

    private fun startFetch(url: URL, executor: Executor): CompletableFuture<ByteArray?> {
        return CompletableFuture.supplyAsync({
            url.getEncodedInputStream()?.use { it.readBytes() }?.let {
                cache[url] = CacheObject(url, it)
                it
            }
        }, executor).apply {
            currentlyFetching[url] = this
        }.whenCompleteAsync { _, _ ->
            currentlyFetching.remove(url)
            pruneCache()
        }
    }

    private fun pruneCache() {
        var cacheSize = cache.values.sumOf { it.size }
        if (cacheSize <= MAX_CACHE_SIZE) return
        val sorted = cache.values.sortedBy { it.lastAccess }
        var i = 0
        while (cacheSize > MAX_CACHE_SIZE) {
            val element = sorted[i]
            cache.remove(element.url)
            cacheSize -= element.size
            i++
        }
    }

    private data class CacheObject(
        val url: URL,
        private val bytes: ByteArray,
        var lastAccess: Long = UMinecraft.getTime()
    ) {
        val size = bytes.size
        fun getBytes(): ByteArray {
            lastAccess = UMinecraft.getTime()
            return bytes
        }
    }

    fun clearCache() {
        for ((url, future) in currentlyFetching) {
            future.cancel(true)
            currentlyFetching.remove(url)
        }
        cache.clear()
    }
}

fun URL.setupConnection(): HttpsURLConnection {
    val con = this.openConnection() as HttpsURLConnection
    con.setRequestProperty("User-Agent", "${ModInfo.NAME}/${ModInfo.VERSION}")
    con.setRequestProperty("Accept-Encoding", "gzip, deflate")
    con.connectTimeout = 20000
    con.readTimeout = 20000
    return con
}

fun URLConnection.getEncodedInputStream(): InputStream? {
    return try {
        val inputStream = this.inputStream
        when (this.contentEncoding) {
            "gzip" -> GZIPInputStream(inputStream)
            "deflate" -> DeflaterInputStream(inputStream)
            else -> inputStream
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun URL.getEncodedInputStream(): InputStream? = this.setupConnection().getEncodedInputStream()

fun URL.getString(useCache: Boolean = true): String? {
    if (useCache) return NetworkUtil.getOrFetch(this)?.decodeToString()
    return this.getEncodedInputStream()?.bufferedReader()?.use { it.readText() }
}

fun URL.getStringAsync(useCache: Boolean = true): CompletableFuture<String?> {
    if (useCache) return NetworkUtil.getOrFetchAsync(this).thenApply { it?.decodeToString() }
    return CompletableFuture.supplyAsync { this.getEncodedInputStream()?.bufferedReader()?.use { it.readText() } }
}

inline fun <reified T> URL.getJson(useCache: Boolean = true): T? {
    return this.getString(useCache)?.fromJson()
}

inline fun <reified T> URL.getJsonAsync(useCache: Boolean = true): CompletableFuture<T?> {
    return this.getStringAsync(useCache).thenApply { it?.fromJson() }
}

fun URL.getImage(
    useCache: Boolean = true,
    width: Float? = null,
    height: Float? = null,
    fit: ImageURLUtils.Fit = ImageURLUtils.Fit.INSIDE
): BufferedImage? {
    val url = ImageURLUtils.getTransformedImageUrl(this.toURI(), width, height, fit).toURL()
    if (useCache) return NetworkUtil.getOrFetch(url, NetworkUtil.ImageThreadPool)?.inputStream()?.use {
        ImageIO.read(it)
    }
    return url.getEncodedInputStream()?.use { ImageIO.read(it) }
}

fun URL.getImageAsync(
    useCache: Boolean = true,
    width: Float? = null,
    height: Float? = null,
    fit: ImageURLUtils.Fit = ImageURLUtils.Fit.INSIDE
): CompletableFuture<BufferedImage> {
    val url = ImageURLUtils.getTransformedImageUrl(this.toURI(), width, height, fit).toURL()
    return if (useCache) return NetworkUtil.getOrFetchAsync(url, NetworkUtil.ImageThreadPool).thenApply { bytes ->
        bytes?.inputStream()?.use { ImageIO.read(it) }
    } else {
        CompletableFuture.supplyAsync(
            { url.getEncodedInputStream()?.use { ImageIO.read(it) } },
            NetworkUtil.ImageThreadPool
        )
    }
}

inline fun <reified T, reified S> URL.postAndGetJson(data: S): T? {
    val con = this.setupConnection()
    val output = data.toJson()
    con.setRequestProperty("Content-Type", "application/json")
    con.setRequestProperty("Content-Length", output.length.toString())
    con.doOutput = true
    con.outputStream.bufferedWriter().use { it.write(output) }
    return con.getEncodedInputStream()?.bufferedReader()?.use { it.fromJson() }
}