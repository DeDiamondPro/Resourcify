/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2026 DeDiamondPro
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
import gg.essential.universal.UMinecraft
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.DeflaterInputStream
import java.util.zip.GZIPInputStream
import javax.net.ssl.HttpsURLConnection

object NetworkUtil {
    private const val MAX_CACHE_SIZE = 100_000_000
    private val cache = ConcurrentHashMap<URI, CacheObject>()
    private val currentlyFetching = ConcurrentHashMap<URI, CompletableFuture<ByteArray?>>()

    fun setupConnection(url: URL): HttpsURLConnection {
        val con = url.openConnection() as HttpsURLConnection
        con.setRequestProperty(
            "User-Agent",
            "${Constants.NAME}/${Constants.VERSION} (${Platform.getMcVersion()}-${Platform.getLoader()})"
        )
        con.setRequestProperty("Accept-Encoding", "gzip, deflate")
        con.connectTimeout = 5000
        con.readTimeout = 5000
        return con
    }

    fun getOrFetch(url: URI, attempts: Int = 1, headers: Map<String, String> = emptyMap()): ByteArray? {
        return cache[url]?.getBytes() ?: currentlyFetching[url]?.get() ?: startFetch(url, attempts, headers).get()
    }

    fun getOrFetchAsync(
        url: URI, attempts: Int = 1,
        headers: Map<String, String> = emptyMap()
    ): CompletableFuture<ByteArray?> {
        return cache[url]?.getBytes()?.let {
            supplyAsync { it }
        } ?: currentlyFetching[url] ?: startFetch(url, attempts, headers)
    }

    private fun startFetch(
        uri: URI, attempts: Int,
        headers: Map<String, String> = emptyMap()
    ): CompletableFuture<ByteArray?> {
        return supplyAsync {
            for (i in 0 until attempts) {
                try {
                    val result = uri.toURL().setupConnection().apply {
                        headers.forEach { (key, value) -> this.setRequestProperty(key, value) }
                    }.getEncodedInputStream()?.use { it.readBytes() }?.let {
                        cache[uri] = CacheObject(uri, it)
                        it
                    }
                    if (result != null) {
                        return@supplyAsync result
                    }
                } catch (e: Exception) {
                    Constants.LOGGER.error("Failed to fetch \"$this\"", e)
                }
                // Wait a bit before trying again
                Thread.sleep(250)
            }
            return@supplyAsync null
        }.apply {
            currentlyFetching[uri] = this
        }.whenComplete { _, _ ->
            currentlyFetching.remove(uri)
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
        val url: URI,
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
            try {
                future.cancel(true)
            } catch (_: Exception) {
            }
            currentlyFetching.remove(url)
        }
        cache.clear()
    }
}

fun URL.setupConnection(): HttpsURLConnection = NetworkUtil.setupConnection(this)

fun URLConnection.getEncodedInputStream(): InputStream? {
    return try {
        val inputStream = this.inputStream
        when (this.contentEncoding) {
            "gzip" -> GZIPInputStream(inputStream)
            "deflate" -> DeflaterInputStream(inputStream)
            else -> inputStream
        }
    } catch (_: Exception) {
        null
    }
}

fun URL.getEncodedInputStream(): InputStream? = this.setupConnection().getEncodedInputStream()

fun URL.getImageInputStream(): InputStream? =
    this.setupConnection().apply { setRequestProperty("Accept", "image/*") }.getEncodedInputStream()

fun URI.getString(useCache: Boolean = true, attempts: Int = 3, headers: Map<String, String> = emptyMap()): String? {
    if (useCache) return NetworkUtil.getOrFetch(this, attempts, headers)?.decodeToString()
    for (i in 0 until attempts) {
        try {
            val result = this.toURL().setupConnection()
                .apply { headers.forEach { (key, value) -> this.setRequestProperty(key, value) } }
                .getEncodedInputStream()?.bufferedReader()?.use { it.readText() }
            if (result != null) {
                return result
            }
        } catch (e: Exception) {
            Constants.LOGGER.error("Failed to fetch string from \"$this\"", e)
        }
        // Wait a bit before trying again
        Thread.sleep(250)
    }
    return null
}

fun URI.getStringAsync(
    useCache: Boolean = true, attempts: Int = 3,
    headers: Map<String, String> = emptyMap()
): CompletableFuture<String?> {
    if (useCache) return NetworkUtil.getOrFetchAsync(this, attempts, headers).thenApply { it?.decodeToString() }
    return supplyAsync { this.getString(false, attempts, headers) }
}

inline fun <reified T> URI.getJson(
    useCache: Boolean = true, attempts: Int = 3,
    headers: Map<String, String> = emptyMap()
): T? {
    return this.getString(useCache, attempts, headers)?.fromJson()
}

inline fun <reified T> URI.getJsonAsync(
    useCache: Boolean = true, attempts: Int = 3,
    headers: Map<String, String> = emptyMap()
): CompletableFuture<T?> {
    return this.getStringAsync(useCache, attempts, headers).thenApply { it?.fromJson() }
}

fun URI.getBytes(useCache: Boolean = true, attempts: Int = 1, headers: Map<String, String> = emptyMap()): ByteArray? {
    if (useCache) {
        return NetworkUtil.getOrFetch(this, attempts, headers)
    }
    for (i in 0 until attempts) {
        try {
            val result = this.toURL().setupConnection()
                .apply { headers.forEach { (key, value) -> this.setRequestProperty(key, value) } }
                .getEncodedInputStream()?.use { it.readBytes() }
            if (result != null) {
                return result
            }
        } catch (e: Exception) {
            Constants.LOGGER.error("Failed to fetch image from \"$this\"", e)
        }
        // Wait a bit before trying again
        Thread.sleep(250)
    }
    return null
}

fun URI.getBytesAsync(
    useCache: Boolean = true,
    attempts: Int = 1,
    headers: Map<String, String> = emptyMap()
): CompletableFuture<ByteArray?> {
    return if (useCache) NetworkUtil.getOrFetchAsync(this, attempts, headers)
    else supplyAsync { getBytes(useCache, attempts, headers) }
}

inline fun <reified S> URI.postAndGetString(
    data: S, attempts: Int = 3,
    headers: Map<String, String> = emptyMap()
): String? {
    for (i in 0 until attempts) {
        try {
            val con = this.toURL().setupConnection()
            val output = data.toJson()
            con.setRequestProperty("Content-Type", "application/json")
            con.setRequestProperty("Content-Length", output.length.toString())
            headers.forEach { (key, value) -> con.setRequestProperty(key, value) }
            con.doOutput = true
            con.outputStream.bufferedWriter().use { it.write(output) }
            val result = con.getEncodedInputStream()?.bufferedReader()?.use { it.readText() }
            if (result != null) {
                return result
            }
        } catch (e: Exception) {
            Constants.LOGGER.error("Failed to post and get string from \"$this\"", e)
        }
        // Wait a bit before trying again
        Thread.sleep(250)
    }
    return null
}

inline fun <reified T, reified S> URI.postAndGetJson(
    data: S, attempts: Int = 3,
    headers: Map<String, String> = emptyMap()
): T? {
    return postAndGetString(data, attempts, headers)?.fromJson()
}

// Ideally this wouldn't be necessary, but CurseForge doesn't encode their URLs properly
fun String.encodeUrl(): String {
    val protocol = this.substringBefore("://", "")
    val path = this.substringAfter("://")
    val host = path.substringBefore("/")
    val query = path.substringAfterLast("?", "")
    val parts = path.substringAfter("/").substringBeforeLast("?").split("/").toMutableList()
    return "${if (protocol.isNotEmpty()) "$protocol://" else ""}$host/${
        parts.joinToString("/") { URLEncoder.encode(it, "UTF-8").replace("+", "%20") }
    }${if (query.isNotEmpty()) "?$query" else ""}"
}

fun String.toURI(): URI = try {
    URI(this)
} catch (_: Exception) {
    URI(this.encodeUrl())
}

fun String.toURIOrNull(): URI? = try {
    this.toURI()
} catch (_: Exception) {
    null
}