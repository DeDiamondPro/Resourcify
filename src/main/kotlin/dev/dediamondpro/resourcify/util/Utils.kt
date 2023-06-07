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

import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.utils.ReleasedDynamicTexture
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.imageio.ImageIO

fun String.capitalizeAll(): String {
    return this.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
        .split("-").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
}

object Utils {
    fun drawTexture(
        matrixStack: UMatrixStack,
        texture: ReleasedDynamicTexture,
        x: Double,
        y: Double,
        textureX: Double,
        textureY: Double,
        width: Double,
        height: Double,
        textureMinFilter: Int = GL11.GL_NEAREST,
        textureMagFilter: Int = GL11.GL_NEAREST
    ) {
        matrixStack.push()

        UGraphics.enableBlend()
        UGraphics.enableAlpha()
        matrixStack.scale(1f, 1f, 50f)
        val glId = texture.dynamicGlId
        UGraphics.bindTexture(0, glId)
        val worldRenderer = UGraphics.getFromTessellator()
        UGraphics.configureTexture(glId) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, textureMinFilter)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, textureMagFilter)
        }

        worldRenderer.beginWithDefaultShader(
            UGraphics.DrawMode.QUADS,
            UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR
        )

        val f = 0.00390625
        worldRenderer.pos(matrixStack, x, y + height, 0.0).tex(textureX * f, (textureY + height) * f)
            .color(1f, 1f, 1f, 1f).endVertex()
        worldRenderer.pos(matrixStack, x + width, y + height, 0.0).tex((textureX + width) * f, (textureY + height) * f)
            .color(1f, 1f, 1f, 1f).endVertex()
        worldRenderer.pos(matrixStack, x + width, y, 0.0).tex((textureX + width) * f, textureY * f)
            .color(1f, 1f, 1f, 1f).endVertex()
        worldRenderer.pos(matrixStack, x + 0, y + 0, 0.0).tex((textureX + 0) * f, (textureY + 0) * f)
            .color(1f, 1f, 1f, 1f).endVertex()
        worldRenderer.drawDirect()

        matrixStack.pop()
    }

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
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }

    fun getShadowColor(color: Color): Color {
        val rgb = color.rgb
        return Color(rgb and 16579836 shr 2 or (rgb and -16777216))
    }

    // In an ideal world this wouldn't be needed, but for some reason it is for some MC versions
    fun readImage(url: URL, inputStream: InputStream): BufferedImage {
        if (!url.file.endsWith("webp")) return ImageIO.read(inputStream)
        val reader = WebPImageReaderSpi().createReaderInstance()
        try {
            reader.input = ImageIO.createImageInputStream(inputStream)
            return reader.read(0)
        } finally {
            reader.dispose()
        }
    }
}