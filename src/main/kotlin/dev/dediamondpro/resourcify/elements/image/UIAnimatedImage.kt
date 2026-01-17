/*
 * This file is part of Resourcify
 * Copyright (C) 2026 DeDiamondPro
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

package dev.dediamondpro.resourcify.elements.image

import dev.dediamondpro.resourcify.util.EmptyImage
import dev.dediamondpro.resourcify.util.supply
import dev.dediamondpro.resourcify.util.supplyAsync
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.image.DefaultFailureImage
import gg.essential.elementa.components.image.DefaultLoadingImage
import gg.essential.elementa.components.image.ImageProvider
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UMinecraft
import org.w3c.dom.Node
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream

class UIAnimatedImage(
    private val framesFuture: CompletableFuture<List<Frame>>,
    private val loadingImage: ImageProvider = DefaultFailureImage,
    private val failureImage: ImageProvider = DefaultFailureImage,
) : IUIImage() {
    data class Frame(var image: BufferedImage?, val frameTime: Int) {
        private var uiImage: UIImage? = null
        var textureMinFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.NEAREST
            set(value) {
                field = value
                uiImage?.textureMinFilter = value
            }
        var textureMagFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.NEAREST
            set(value) {
                field = value
                uiImage?.textureMagFilter = value
            }

        fun getOrCommit(): UIImage {
            if (uiImage == null) {
                commitIfNeed()
            }

            return uiImage!!
        }

        fun commitIfNeed() {
            if (uiImage != null) {
                return
            }
            uiImage = UIImage(supply { image ?: error("No image provided!") }, loadingImage = EmptyImage)
            image = null // Let the buffered image be garbage collected
        }
    }

    private var frames: List<Frame>? = null
    private var currentFrame = 0
    private var lastFrameTime = -1L

    override var imageWidth = 0f
    override var imageHeight = 0f
    override var textureMinFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.NEAREST
        set(value) {
            field = value
            frames?.forEach { it.textureMinFilter = value }
        }
    override var textureMagFilter: UIImage.TextureScalingMode = UIImage.TextureScalingMode.NEAREST
        set(value) {
            field = value
            frames?.forEach { it.textureMagFilter = value }
        }

    init {
        framesFuture.exceptionally {
            it.printStackTrace()
            return@exceptionally null
        }.thenAccept {
            frames = it
            it?.firstOrNull()?.let { frame ->
                imageWidth = frame.getOrCommit().imageWidth
                imageHeight = frame.getOrCommit().imageHeight
            }
            it?.forEach { frame ->
                frame.textureMinFilter = textureMinFilter
                frame.textureMagFilter = textureMagFilter
            }
            commitAhead()
        }
    }

    override fun drawImage(
        matrixStack: UMatrixStack,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        color: Color
    ) {
        when {
            frames != null -> {
                frames!![currentFrame].getOrCommit().drawImage(matrixStack, x, y, width, height, color)

                if (lastFrameTime != -1L) {
                    val now = UMinecraft.getTime()
                    var diff = now - lastFrameTime

                    // Make it so we skip frames when needed
                    var updated = false
                    while (diff >= frames!![currentFrame].frameTime) {
                        diff -= frames!![currentFrame].frameTime
                        currentFrame = (currentFrame + 1) % frames!!.size
                        updated = true
                    }

                    if (updated) {
                        lastFrameTime = now
                        commitAhead()
                    }
                } else {
                    lastFrameTime = UMinecraft.getTime()
                }
            }

            framesFuture.isCompletedExceptionally -> {
                failureImage.drawImageCompat(matrixStack, x, y, width, height, color)
            }

            else -> loadingImage.drawImageCompat(matrixStack, x, y, width, height, color)
        }
    }

    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        val x = this.getLeft().toDouble()
        val y = this.getTop().toDouble()
        val width = this.getWidth().toDouble()
        val height = this.getHeight().toDouble()
        val color = this.getColor()

        if (color.alpha == 0) {
            return super.draw(matrixStack)
        }

        this.drawImage(matrixStack, x, y, width, height, color)

        super.draw(matrixStack)
    }

    private fun commitAhead() {
        if (frames == null) return
        // Commit 1 frame ahead for smooth playback
        for (i in 0..1) {
            val frame = (currentFrame + i) % frames!!.size
            frames!![frame].commitIfNeed()
        }
    }

    companion object {
        fun fromGif(
            stream: InputStream,
            loadAsync: Boolean = true,
            loadingImage: ImageProvider = DefaultLoadingImage,
            failureImage: ImageProvider = DefaultFailureImage,
        ): UIAnimatedImage {
            return UIAnimatedImage(
                if (loadAsync) supplyAsync { provideFrames(stream) }
                else supply { provideFrames(stream) },
                loadingImage, failureImage
            )
        }

        fun fromGif(
            bytes: CompletableFuture<ByteArray?>,
            loadingImage: ImageProvider = DefaultLoadingImage,
            failureImage: ImageProvider = DefaultFailureImage,
        ): UIAnimatedImage {
            return UIAnimatedImage(
                bytes.thenApply {
                    provideFrames(it?.inputStream() ?: error("Failed to load bytes"))
                }, loadingImage, failureImage
            )
        }

        fun supportsExtension(extension: String): Boolean {
            return extension == "gif"
        }

        private fun provideFrames(stream: InputStream): List<Frame> {
            val reader = getGifReader(ImageIO.createImageInputStream(stream))

            val width = reader.getWidth(0)
            val height = reader.getHeight(0)

            // Master canvas to draw to for optimized GIFs
            var masterCanvas = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

            val frameCount = reader.getNumImages(true)
            val frames = mutableListOf<Frame>()
            for (i in 0 until frameCount) {
                // Extract the frame and it's metadata
                val frame = reader.read(i)
                val metadata = reader.getImageMetadata(i)

                // Metadata defaults
                var delay = 100
                var disposal = "none"
                var x = 0
                var y = 0

                // Read the metadata
                fun walk(node: Node) {
                    when (node.nodeName) {
                        "GraphicControlExtension" -> {
                            node.attributes.getNamedItem("delayTime")?.let { delay = it.nodeValue.toInt() * 10 }
                            node.attributes.getNamedItem("disposalMethod")?.let { disposal = it.nodeValue }
                        }

                        "ImageDescriptor" -> {
                            node.attributes.getNamedItem("imageLeftPosition")?.let { x = it.nodeValue.toInt() }
                            node.attributes.getNamedItem("imageTopPosition")?.let { y = it.nodeValue.toInt() }
                        }

                        else -> {
                            var child = node.firstChild
                            while (child != null) {
                                walk(child)
                                child = child.nextSibling
                            }
                        }
                    }
                }
                walk(metadata.getAsTree("javax_imageio_gif_image_1.0"))
                // If there is no frame delay, we'll use 100 ms
                if (delay <= 0) {
                    delay = 100
                }

                // If we need to restore to the current state, save this state
                var prevState: BufferedImage? = null
                if (disposal == "restoreToPrevious") {
                    prevState = copyImage(masterCanvas);
                }

                // Draw the current frame on to the master canvas
                val g = masterCanvas.createGraphics()
                g.drawImage(frame, x, y, null)

                // Copy the master canvas , this will store the frame
                frames.add(Frame(copyImage(masterCanvas), delay))

                // Apply the disposal method if needed
                when (disposal) {
                    "restoreToBackgroundColor" -> {
                        g.background = Color(0, 0, 0, 0)
                        g.clearRect(x, y, frame.width, frame.height)
                    }

                    "restoreToPrevious" -> {
                        prevState?.let { masterCanvas = it }
                    }
                }
                g.dispose()
            }

            return frames
        }

        private fun copyImage(src: BufferedImage): BufferedImage {
            val copy = BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_ARGB)
            val g = copy.createGraphics()
            g.drawImage(src, 0, 0, null)
            g.dispose()
            return copy
        }

        private fun getGifReader(stream: ImageInputStream): ImageReader {
            val readers = ImageIO.getImageReadersByFormatName("gif")
            if (!readers.hasNext()) {
                error("No GIF ImageReader found")
            }
            val reader = readers.next()
            reader.input = stream
            return reader
        }
    }
}