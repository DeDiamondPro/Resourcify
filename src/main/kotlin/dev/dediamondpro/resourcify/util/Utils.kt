/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2025 DeDiamondPro
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
import net.minecraft.client.resources.language.I18n
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation
*///?} else
import net.minecraft.resources.Identifier

fun String.capitalizeAll(): String {
    return this.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
        .split("-").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
}

@JvmName("localizeExtension")
fun String.localize(vararg parameters: Any): String {
    return I18n.get(this, *parameters)
}

fun localize(key: String, vararg parameters: Any): String {
    return I18n.get(key, *parameters)
}

@JvmName("localizeOrDefaultExtension")
fun String.localizeOrDefault(default: String, vararg parameters: Any): String {
    val formatted = I18n.get(this, *parameters)
    return if (formatted == this) default.format(*parameters) else formatted
}

fun localizeOrDefault(key: String, default: String, vararg parameters: Any): String {
    val formatted = I18n.get(key, *parameters)
    return if (formatted == key) default.format(*parameters) else formatted
}

object Utils {
    fun getSha1(file: File): String? {
        try {
            FileInputStream(file).use { it ->
                val digest: MessageDigest = MessageDigest.getInstance("SHA-1")
                val buffer = ByteArray(1024)
                var count: Int
                while (it.read(buffer).also { count = it } != -1) {
                    digest.update(buffer, 0, count)
                }
                val digested: ByteArray = digest.digest()
                val sb = StringBuilder()
                for (b in digested) {
                    sb.append(((b.toInt() and 0xff) + 0x100).toString(16).substring(1))
                }
                return sb.toString()
            }
        } catch (e: IOException) {
            Constants.LOGGER.error("Failed to get SHA-1 for file \"$file\"", e)
        } catch (e: NoSuchAlgorithmException) {
            Constants.LOGGER.error("Failed to get SHA-1 for file \"$file\"", e)
        }
        return null
    }

    fun getShadowColor(color: Color): Color {
        val rgb = color.rgb
        return Color(rgb and 16579836 shr 2 or (rgb and -16777216))
    }

    fun incrementFileName(fileName: String): String {
        val regex = """\((\d+)\)(\.\w+)?$""".toRegex()
        val matchResult = regex.find(fileName)

        return if (matchResult != null) {
            val currentNumber = matchResult.groupValues[1].toInt()
            val extension = matchResult.groupValues[2]
            fileName.replace(regex, "(${currentNumber + 1})$extension")
        } else {
            val dotIndex = fileName.lastIndexOf('.')
            if (dotIndex != -1) {
                fileName.substring(0, dotIndex) + " (1)." + fileName.substring(dotIndex + 1)
            } else {
                "$fileName (1)"
            }
        }
    }

    fun createResourceLocation(asset: String): /*? if <1.21.11 {*/ /*ResourceLocation *//*?} else {*/Identifier /*?}*/ {
        //? if <1.21.0 {
        /*return ResourceLocation("resourcify", asset)
        *///?} else if <1.21.11 {
        /*return ResourceLocation.fromNamespaceAndPath("resourcify", asset)
        *///?} else
        return Identifier.fromNamespaceAndPath("resourcify", asset)
    }
}