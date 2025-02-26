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
    private val defaults: Map<String, String> =
        this.javaClass.getResourceAsStream("/assets/resourcify/colors.json")!!.bufferedReader().fromJson()

    lateinit var TEXT_PRIMARY: Color
        private set
    lateinit var TEXT_SECONDARY: Color
        private set
    lateinit var TEXT_LINK: Color
        private set
    lateinit var TEXT_WARN: Color
        private set
    lateinit var BUTTON_PRIMARY: Color
        private set
    lateinit var BUTTON_SECONDARY: Color
        private set
    lateinit var BUTTON_PRIMARY_DISABLED: Color
        private set
    lateinit var BUTTON_SECONDARY_DISABLED: Color
        private set
    lateinit var CHECKBOX: Color
        private set
    lateinit var EXPANDABLE: Color
        private set
    lateinit var BACKGROUND: Color
        private set
    lateinit var AD_BACKGROUND: Color
        private set
    lateinit var FULLSCREEN_BACKGROUND: Color
        private set
    lateinit var DROPDOWN: Color
        private set
    lateinit var DROPDOWN_SELECTED: Color
        private set
    lateinit var DROPDOWN_BORDER: Color
        private set

    lateinit var MARKDOWN_STYLE: MarkdownStyle
        private set

    init {
        load(emptyMap())
    }

    fun load(colors: Map<String, String>) {
        // Set everything back to defaults and load the new colors
        loadInternal(defaults)
        loadInternal(colors)

        MARKDOWN_STYLE = MarkdownStyle(
            textStyle = MarkdownTextStyle(1f, TEXT_PRIMARY, 2f, DefaultFonts.VANILLA_FONT_RENDERER),
            imageStyle = ImageStyleConfig(SanitizingImageProvider),
            linkStyle = LinkStyleConfig(TEXT_LINK, ConfirmingBrowserProvider)
        )
    }

    private fun loadInternal(colors: Map<String, String>) {
        TEXT_PRIMARY = colors["text_primary"]?.toColor() ?: TEXT_PRIMARY
        TEXT_SECONDARY = colors["text_secondary"]?.toColor() ?: TEXT_SECONDARY
        TEXT_LINK = colors["text_link"]?.toColor() ?: TEXT_LINK
        TEXT_WARN = colors["text_warn"]?.toColor() ?: TEXT_WARN
        BUTTON_PRIMARY = colors["button_primary"]?.toColor() ?: BUTTON_PRIMARY
        BUTTON_SECONDARY = colors["button_secondary"]?.toColor() ?: BUTTON_SECONDARY
        BUTTON_PRIMARY_DISABLED = colors["button_primary_disabled"]?.toColor() ?: BUTTON_PRIMARY_DISABLED
        BUTTON_SECONDARY_DISABLED = colors["button_secondary_disabled"]?.toColor() ?: BUTTON_SECONDARY_DISABLED
        CHECKBOX = colors["checkbox"]?.toColor() ?: CHECKBOX
        EXPANDABLE = colors["expandable"]?.toColor() ?: EXPANDABLE
        BACKGROUND = colors["background"]?.toColor() ?: BACKGROUND
        AD_BACKGROUND = colors["ad_background"]?.toColor() ?: AD_BACKGROUND
        FULLSCREEN_BACKGROUND = colors["fullscreen_background"]?.toColor() ?: FULLSCREEN_BACKGROUND
        DROPDOWN = colors["dropdown"]?.toColor() ?: DROPDOWN
        DROPDOWN_SELECTED = colors["dropdown_selected"]?.toColor() ?: DROPDOWN_SELECTED
        DROPDOWN_BORDER = colors["dropdown_border"]?.toColor() ?: DROPDOWN_BORDER
    }

    private fun String.toColor(): Color {
        val hex = this.replace("#", "").trim()
        if (hex.length != 6 && hex.length != 8) {
            error("Resourcify: Invalid hex color format \"$this\" in a resource pack's colors.json!")
        }
        val r = Integer.parseInt(hex.substring(0, 2), 16)
        val g = Integer.parseInt(hex.substring(2, 4), 16)
        val b = Integer.parseInt(hex.substring(4, 6), 16)
        val a = if (hex.length == 8) Integer.parseInt(hex.substring(6, 8), 16) else 255
        return Color(r, g, b, a)
    }
}