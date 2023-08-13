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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.GZIPInputStream
import javax.net.ssl.HttpsURLConnection

val json = Json {
    encodeDefaults = true
    prettyPrint = false
    ignoreUnknownKeys = true
    isLenient = true
}

object NetworkUtil {
    private const val MAX_CACHE_SIZE = 100_000_000
    private val cache = ConcurrentHashMap<URL, CacheObject>()

    fun getOrFetch(url: URL): ByteArray? {
        val value = cache[url]
        return if (value == null) {
            val bytes = url.getEncodedInputStream()?.use { it.readBytes() }
            if (bytes != null) {
                cache[url] = CacheObject(url, bytes)
                pruneCache()
            }
            bytes
        } else {
            value.getBytes()
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
        cache.clear()
    }
}

fun URL.setupConnection(): HttpsURLConnection {
    val con = this.openConnection() as HttpsURLConnection
    con.setRequestProperty("User-Agent", "${ModInfo.NAME}/${ModInfo.VERSION}")
    con.setRequestProperty("Accept-Encoding", "gzip")
    con.connectTimeout = 5000
    con.readTimeout = 5000
    return con
}

fun URLConnection.getEncodedInputStream(): InputStream? {
    return try {
        val inputStream = this.inputStream
        if (this.contentEncoding == "gzip") GZIPInputStream(inputStream) else inputStream
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

inline fun <reified T> URL.getJson(useCache: Boolean = true): T? {
    return this.getString(useCache)?.let { json.decodeFromString(it) }
}

fun URL.getImage(useCache: Boolean = true): BufferedImage? {
    if (useCache) return NetworkUtil.getOrFetch(this)?.inputStream()?.use { Utils.readImage(this, it) }
    return this.getEncodedInputStream()?.use { Utils.readImage(this, it) }
}

inline fun <reified T, reified S : Any> URL.postAndGetJson(data: S): T? {
    val con = this.setupConnection()
    val output = json.encodeToString(data)
    con.setRequestProperty("Content-Type", "application/json")
    con.setRequestProperty("Content-Length", output.length.toString())
    con.doOutput = true
    con.outputStream.bufferedWriter().use { it.write(output) }
    return con.getEncodedInputStream()?.bufferedReader()?.use { json.decodeFromString(it.readText()) }
}