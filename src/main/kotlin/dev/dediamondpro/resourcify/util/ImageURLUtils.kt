package dev.dediamondpro.resourcify.util

import org.apache.http.client.utils.URIBuilder
import java.net.URI
import javax.imageio.ImageIO

object ImageURLUtils {
    private val urlExtensionRegex: Regex = Regex(".*\\.(\\w+)\$")

    // Hostnames that won't be automatically proxied, same one is used by modrinth
    // Taken from https://github.com/modrinth/omorphia/blob/2ed06a96fec38b81ab58bdac0c2bb667960ca1c2/lib/helpers/parse.js#L78
    private val allowedHostNames = listOf(
        "imgur.com",
        "i.imgur.com",
        "cdn-raw.modrinth.com",
        "cdn.modrinth.com",
        "staging-cdn-raw.modrinth.com",
        "staging-cdn.modrinth.com",
        "github.com",
        "raw.githubusercontent.com",
        "user-images.githubusercontent.com",
        "avatars.githubusercontent.com",
        "img.shields.io",
        "raster.shields.io",
        "i.postimg.cc",
        "wsrv.nl",
        "cf.way2muchnoise.eu",
        "bstats.org",
    )

    fun getTransformedImageUrl(
        url: URI,
        width: Float? = null,
        height: Float? = null,
        fit: Fit = Fit.INSIDE
    ): URI {
        val canReadType = hasImageReaderFor(url.rawPath)
        val useProxy = !allowedHostNames.contains(url.host) || !canReadType || width != null || height != null
        return if (!useProxy) {
            if (url.host == "img.shields.io") {
                URIBuilder(url).setHost("raster.shields.io").build()
            } else {
                url
            }
        } else {
            URIBuilder("https://wsrv.nl/").apply {
                addParameter("url", url.toString())
                if (!canReadType) addParameter("output", "png")
                if (width != null) addParameter("w", width.toString())
                if (height != null) addParameter("h", height.toString())
                if (width != null || height != null) {
                    addParameter("fit", fit.name.lowercase())
                    addParameter("we", "")
                }
            }.build()
        }
    }

    private fun hasImageReaderFor(url: String): Boolean {
        val extension = urlExtensionRegex.replace(url, "$1")
        return extension == url || ImageIO.getImageReadersBySuffix(extension).hasNext()
    }

    enum class Fit {
        INSIDE,
        OUTSIDE,
        COVER,
        FILL,
        CONTAIN
    }
}