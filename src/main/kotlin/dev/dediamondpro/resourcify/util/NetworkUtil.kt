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

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import dev.dediamondpro.resourcify.ModInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.util.zip.GZIPInputStream
import javax.net.ssl.HttpsURLConnection
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

val json = Json {
    encodeDefaults = true
    prettyPrint = false
    ignoreUnknownKeys = true
    isLenient = true
}

object NetworkUtil {
    val cache: LoadingCache<URL, ByteArray> = Caffeine.newBuilder()
        .expireAfterAccess(5.minutes.toJavaDuration())
        .maximumWeight(100_000_000)
        .weigher<URL, ByteArray> { _, bytes -> bytes.size }
        .build { it.getEncodedInputStream()?.use { stream -> stream.readBytes() } }

    fun clearCache() {
        cache.invalidateAll()
        cache.cleanUp()
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
    if (useCache) return NetworkUtil.cache[this]?.decodeToString()
    return this.getEncodedInputStream()?.bufferedReader()?.use { it.readText() }
}

inline fun <reified T> URL.getJson(useCache: Boolean = true): T? {
    return this.getString(useCache)?.let { json.decodeFromString(it) }
}

fun URL.getImage(useCache: Boolean = true): BufferedImage? {
    if (useCache) return NetworkUtil.cache[this]?.inputStream()?.use { Utils.readImage(this, it) }
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