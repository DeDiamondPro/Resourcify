package dev.dediamondpro.resourcify.services

import dev.dediamondpro.resourcify.util.localize
import java.awt.Color

enum class VersionType(val color: Color) {
    RELEASE(Color(27, 217, 106)),
    BETA(Color(255, 163, 71)),
    ALPHA(Color(255, 73, 110));

    val localizedName: String
        get() = "resourcify.version.type.${name.lowercase()}".localize()

    companion object {
        fun fromName(name: String): VersionType {
            return values().firstOrNull { it.name.equals(name, ignoreCase = true) } ?: RELEASE
        }
    }
}