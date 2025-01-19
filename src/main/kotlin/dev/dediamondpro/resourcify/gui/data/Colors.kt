/*
 * This file is part of Resourcify
 * Copyright (C) 2025 DeDiamondPro
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

package dev.dediamondpro.resourcify.gui.data

import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.elementa.style.MarkdownTextStyle
import dev.dediamondpro.minemark.style.ImageStyleConfig
import dev.dediamondpro.minemark.style.LinkStyleConfig
import dev.dediamondpro.resourcify.util.ConfirmingBrowserProvider
import dev.dediamondpro.resourcify.util.SanitizingImageProvider
import dev.dediamondpro.resourcify.util.fromJson
import gg.essential.elementa.font.DefaultFonts
import java.awt.Color

object Colors {
    // Load defaults and set default markdown style
    private val defaults: Map<String, JsonColor> =
        this.javaClass.getResourceAsStream("/assets/resourcify/colors.json")!!.bufferedReader().fromJson()

    lateinit var PRIMARY: Color
        private set
    lateinit var SECONDARY: Color
        private set
    lateinit var LINK: Color
        private set
    lateinit var WARN: Color
        private set
    lateinit var BUTTON: Color
        private set
    lateinit var BUTTON_SECONDARY: Color
        private set
    lateinit var BACKGROUND: Color
        private set
    lateinit var AD_BACKGROUND: Color
        private set
    lateinit var DROPDOWN: Color
        private set
    lateinit var DROPDOWN_SELECTED: Color
        private set

    lateinit var MARKDOWN_STYLE: MarkdownStyle
        private set

    init {
        load(emptyMap())
    }

    fun load(colors: Map<String, JsonColor>) {
        // Set everything back to defaults and load the new colors
        loadInternal(defaults)
        loadInternal(colors)

        MARKDOWN_STYLE = MarkdownStyle(
            textStyle = MarkdownTextStyle(1f, PRIMARY, 2f, DefaultFonts.VANILLA_FONT_RENDERER),
            imageStyle = ImageStyleConfig(SanitizingImageProvider),
            linkStyle = LinkStyleConfig(LINK, ConfirmingBrowserProvider)
        )
    }

    private fun loadInternal(colors: Map<String, JsonColor>) {
        PRIMARY = colors["primary"]?.toColor() ?: PRIMARY
        SECONDARY = colors["secondary"]?.toColor() ?: SECONDARY
        LINK = colors["link"]?.toColor() ?: LINK
        WARN = colors["warn"]?.toColor() ?: WARN
        BUTTON = colors["button"]?.toColor() ?: BUTTON
        BUTTON_SECONDARY = colors["button_secondary"]?.toColor() ?: BUTTON_SECONDARY
        BACKGROUND = colors["background"]?.toColor() ?: BACKGROUND
        AD_BACKGROUND = colors["ad_background"]?.toColor() ?: AD_BACKGROUND
        DROPDOWN = colors["dropdown"]?.toColor() ?: DROPDOWN
        DROPDOWN_SELECTED = colors["dropdown_selected"]?.toColor() ?: DROPDOWN_SELECTED
    }

    // Defaults primarily as backup if colors.json is missing a color
//    private fun setDefaults() {
//        PRIMARY = Color.WHITE
//        SECONDARY = Color.LIGHT_GRAY
//        LINK = Color(65, 105, 225)
//        WARN = Color.YELLOW
//        BUTTON = Color(27, 217, 106)
//        BUTTON_SECONDARY = Color(150, 150, 150)
//        BACKGROUND = Color(0, 0, 0, 100)
//        AD_BACKGROUND = Color(60, 130, 255, 100)
//        DROPDOWN = Color(0, 0, 0, 200)
//        DROPDOWN_SELECTED = Color(27, 217, 106, 200)
//    }

    data class JsonColor(val hex: String, val opacity: Float) {
        fun toColor(): Color {
            val cleaned = hex.replace("#", "").trim()
            val r = Integer.parseInt(cleaned.substring(0, 2), 16)
            val g = Integer.parseInt(cleaned.substring(2, 4), 16)
            val b = Integer.parseInt(cleaned.substring(4, 6), 16)
            return Color(r / 255f, g / 255f, b / 255f, opacity.coerceAtLeast(0f).coerceAtMost(1f))
        }
    }
}