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

package dev.dediamondpro.resourcify.util

import dev.dediamondpro.resourcify.ModInfo
import gg.essential.universal.UMinecraft
import org.apache.http.client.utils.URIBuilder
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.security.KeyStore
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.DeflaterInputStream
import java.util.zip.GZIPInputStream
import javax.imageio.ImageIO
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

object NetworkUtil {
    private const val MAX_CACHE_SIZE = 100_000_000
    private val cache = ConcurrentHashMap<URL, CacheObject>()
    private val currentlyFetching = ConcurrentHashMap<URL, CompletableFuture<ByteArray?>>()

    //#if MC < 11700
    private var sslContext: SSLContext? = null

    // In case of MC 1.16.5 or lower, we need to load a custom keystore since the java version bundled with the Minecraft Launcher doesn't have the Let's Encrypt CA
    init {
        try {
            val keyStore = KeyStore.getInstance("JKS")
            keyStore.load(
                this::class.java.getResourceAsStream("/ssl/resourcify-keystore.jks"),
                "Resourcify".toCharArray()
            )
            val keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            val trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            keyManager.init(keyStore, null)
            trustManager.init(keyStore)
            sslContext = SSLContext.getInstance("TLS").apply {
                init(keyManager.keyManagers, trustManager.trustManagers, null)
            }
        } catch (e: Exception) {
            println("Failed to load Resourcify keystore, api requests may not work properly.")
            e.printStackTrace()
        }
    }
    //#endif

    fun setupConnection(url: URL): HttpsURLConnection {
        val con = url.openConnection() as HttpsURLConnection
        //#if MC < 11700
        sslContext?.let { con.sslSocketFactory = it.socketFactory }
        //#endif
        con.setRequestProperty("User-Agent", "${ModInfo.NAME}/${ModInfo.VERSION}")
        con.setRequestProperty("Accept-Encoding", "gzip, deflate")
        con.connectTimeout = 5000
        con.readTimeout = 5000
        return con
    }

    fun getOrFetch(url: URL, attempts: Int = 1, headers: Map<String, String> = emptyMap()): ByteArray? {
        return cache[url]?.getBytes() ?: currentlyFetching[url]?.get() ?: startFetch(url, attempts, headers).get()
    }

    fun getOrFetchAsync(
        url: URL, attempts: Int = 1,
        headers: Map<String, String> = emptyMap()
    ): CompletableFuture<ByteArray?> {
        return cache[url]?.getBytes()?.let {
            supply { it }
        } ?: currentlyFetching[url] ?: startFetch(url, attempts, headers)
    }

    private fun startFetch(
        url: URL, attempts: Int,
        headers: Map<String, String> = emptyMap()
    ): CompletableFuture<ByteArray?> {
        return supplyAsync {
            for (i in 0 until attempts) {
                try {
                    val result = url.setupConnection().apply {
                        headers.forEach { (key, value) -> this.setRequestProperty(key, value) }
                    }.getEncodedInputStream()?.use { it.readBytes() }?.let {
                        cache[url] = CacheObject(url, it)
                        it
                    }
                    if (result != null) {
                        return@supplyAsync result
                    }
                } catch (_: Exception) {
                }
                // Wait a bit before trying again
                Thread.sleep(250)
            }
            return@supplyAsync null
        }.apply {
            currentlyFetching[url] = this
        }.whenComplete { _, _ ->
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

fun URL.getString(useCache: Boolean = true, attempts: Int = 3, headers: Map<String, String> = emptyMap()): String? {
    if (useCache) return NetworkUtil.getOrFetch(this, attempts, headers)?.decodeToString()
    for (i in 0 until attempts) {
        try {
            val result = this.setupConnection()
                .apply { headers.forEach { (key, value) -> this.setRequestProperty(key, value) } }
                .getEncodedInputStream()?.bufferedReader()?.use { it.readText() }
            if (result != null) {
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Wait a bit before trying again
        Thread.sleep(250)
    }
    return null
}

fun URL.getStringAsync(
    useCache: Boolean = true, attempts: Int = 3,
    headers: Map<String, String> = emptyMap()
): CompletableFuture<String?> {
    if (useCache) return NetworkUtil.getOrFetchAsync(this, attempts, headers).thenApply { it?.decodeToString() }
    return supplyAsync { this.getString(false, attempts, headers) }
}

inline fun <reified T> URL.getJson(
    useCache: Boolean = true, attempts: Int = 3,
    headers: Map<String, String> = emptyMap()
): T? {
    return this.getString(useCache, attempts, headers)?.fromJson()
}

inline fun <reified T> URL.getJsonAsync(
    useCache: Boolean = true, attempts: Int = 3,
    headers: Map<String, String> = emptyMap()
): CompletableFuture<T?> {
    return this.getStringAsync(useCache, attempts, headers).thenApply { it?.fromJson() }
}

fun URL.getImage(
    useCache: Boolean = true,
    width: Float? = null,
    height: Float? = null,
    fit: ImageURLUtils.Fit = ImageURLUtils.Fit.INSIDE,
    attempts: Int = 1
): BufferedImage? {
    val url = ImageURLUtils.getTransformedImageUrl(this.toURI(), width, height, fit).toURL()
    if (useCache) return NetworkUtil.getOrFetch(url, attempts)?.inputStream()?.use {
        ImageIO.read(it)
    }
    for (i in 0 until attempts) {
        try {
            val result = url.getEncodedInputStream()?.use { ImageIO.read(it) }
            if (result != null) {
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Wait a bit before trying again
        Thread.sleep(250)
    }
    return null

}

fun URL.getImageAsync(
    useCache: Boolean = true,
    width: Float? = null,
    height: Float? = null,
    fit: ImageURLUtils.Fit = ImageURLUtils.Fit.INSIDE,
    attempts: Int = 1
): CompletableFuture<BufferedImage> {
    val url = ImageURLUtils.getTransformedImageUrl(this.toURI(), width, height, fit).toURL()
    return if (useCache) NetworkUtil.getOrFetchAsync(url, attempts)
        .thenApply { bytes ->
            bytes?.inputStream()?.use { ImageIO.read(it) }
        } else supplyAsync { this.getImage(false, width, height, fit, attempts)!! }
}

inline fun <reified S> URL.postAndGetString(
    data: S, attempts: Int = 3,
    headers: Map<String, String> = emptyMap()
): String? {
    for (i in 0 until attempts) {
        try {
            val con = this.setupConnection()
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
            e.printStackTrace()
        }
        // Wait a bit before trying again
        Thread.sleep(250)
    }
    return null
}

inline fun <reified T, reified S> URL.postAndGetJson(
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

fun String.toURL(): URL = this.toURI().toURL()