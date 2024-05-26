package dev.dediamondpro.resourcify.util

import dev.dediamondpro.minemark.providers.DefaultImageProvider
import java.io.InputStream
import java.net.URI

object SanitizingImageProvider : DefaultImageProvider() {
    override fun getInputStream(src: String): InputStream? {
        return ImageURLUtils.getTransformedImageUrl(src.toURI()).toURL().setupConnection().apply {
            setRequestProperty("Accept", "image/*")
        }.getEncodedInputStream()
    }
}