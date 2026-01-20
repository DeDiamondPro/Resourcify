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
import gg.essential.elementa.components.Window
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
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

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

        fun get(): UIImage? {
            return uiImage
        }

        fun getOrCommit(forceCommit: Boolean = false): UIImage? {
            if (uiImage == null) {
                commitIfNeed(forceCommit)
                return if (forceCommit) uiImage else null
            }

            return uiImage
        }

        private fun commitIfNeed(forceCommit: Boolean = false) {
            if (uiImage != null || !allowedToCommit() && !forceCommit) {
                return
            }

            uiImage = UIImage(supply { image ?: error("No image provided!") }, loadingImage = EmptyImage)
            image = null // Let the buffered image be garbage collected
        }

        companion object {
            private val commitCounter = AtomicInteger(0)

            private fun allowedToCommit(): Boolean {
                // Commit a max of 1 GIF frames per rendered frame to prevent performance issues
                val value = commitCounter.getAndIncrement()
                val allowed = value < 1
                if (value == 0) {
                    Window.enqueueRenderOperation {
                        // Next frame reset the count
                        commitCounter.set(0)
                    }
                }
                return allowed
            }
        }
    }

    private var frames: List<Frame>? = null
    private var currentFrame = 0
    private var lastFrameTime = -1L

    override var imageWidth = 1f
    override var imageHeight = 1f
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

    override fun isLoaded(): Boolean {
        return frames?.firstOrNull()?.get()?.isLoaded ?: false
    }

    init {
        framesFuture.exceptionally {
            it.printStackTrace()
            return@exceptionally null
        }.thenAccept {
            frames = it
            it?.firstOrNull()?.let { frame ->
                imageWidth = frame.getOrCommit(true)!!.imageWidth
                imageHeight = frame.getOrCommit(true)!!.imageHeight
            }
            it?.forEach { frame ->
                frame.textureMinFilter = textureMinFilter
                frame.textureMagFilter = textureMagFilter
            }
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
        var frame = frames?.get(currentFrame)?.getOrCommit()
        // Try to see if any of the previous frames are commited and usable while we wait for this one to commit
        if (frames != null && frame == null) {
            var frameI = currentFrame
            while (--frameI >= 0 && frame == null) {
                frame = frames?.get(frameI)?.get()
            }
        }

        when {
            frame != null -> {
                frame.drawImage(matrixStack, x, y, width, height, color)

                if (lastFrameTime != -1L) {
                    val now = UMinecraft.getTime()
                    var diff = now - lastFrameTime

                    // Make it so we skip frames when needed
                    while (diff >= frames!![currentFrame].frameTime) {
                        diff -= frames!![currentFrame].frameTime
                        currentFrame = (currentFrame + 1) % frames!!.size
                        lastFrameTime = now
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

    companion object {
        fun supportsExtension(extension: String): Boolean {
            return extension == "gif"
        }

        fun provideFrames(stream: InputStream): List<Frame> {
            val reader = getGifReader(ImageIO.createImageInputStream(stream))

            val frames = mutableListOf<Frame>()
            try {
                val width = reader.getWidth(0)
                val height = reader.getHeight(0)

                // Limit the frame count to prevent loading huge images into vram
                // Max frame count => 100MB worth of data
                val dataSize = width * height * 4
                val maxFrameCount = max(1, floor(100f * 1024 * 1024 / dataSize.toFloat()).toInt())

                // Master canvas to draw to for optimized GIFs
                var masterCanvas = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

                val frameCount = reader.getNumImages(true)
                for (i in 0 until min(frameCount, maxFrameCount)) {
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
            } finally {
                reader.dispose()
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