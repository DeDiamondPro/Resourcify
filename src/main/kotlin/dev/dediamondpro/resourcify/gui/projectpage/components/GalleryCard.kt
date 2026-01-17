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

package dev.dediamondpro.resourcify.gui.projectpage.components

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.constraints.CustomImageAspectConstraint
import dev.dediamondpro.resourcify.constraints.ImageFillConstraint
import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.services.IGalleryImage
import dev.dediamondpro.resourcify.util.ofURLCustom
import dev.dediamondpro.resourcify.util.toURIOrNull
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UKeyboard

class GalleryCard(gallery: IGalleryImage) : UIBlock(Colors.BACKGROUND) {
    init {
        constrain {
            x = 0.pixels()
            y = 0.pixels()
            height = ChildLocationSizeConstraint()
        }
        onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            val background = UIBlock(Colors.FULLSCREEN_BACKGROUND).constrain {
                x = 0.pixels()
                y = 0.pixels()
                width = 100.percent()
                height = 100.percent()
            }.onFocusLost {
                isFloating = false
                Window.of(this).removeChild(this)
            }.onKeyType { _, keyCode ->
                if (keyCode != UKeyboard.KEY_ESCAPE) return@onKeyType
                releaseWindowFocus()
                isFloating = false
                Window.of(this).removeChild(this)
            } childOf Window.of(this)
            background.isFloating = true
            background.grabWindowFocus()
            gallery.url.toURIOrNull()?.let { image ->
                UIImage.ofURLCustom(image, true).constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    width = ImageFillConstraint()
                    height = ImageFillConstraint()
                } childOf background
            }
        }
        gallery.getThumbnailUrlIfEnabled()?.let { image ->
            UIImage.ofURLCustom(image, false).constrain {
                x = 0.pixels()
                y = 0.pixels()
                width = 100.percent()
                height = CustomImageAspectConstraint()
            } childOf this
        }
        if (!gallery.title.isNullOrBlank()) UIWrappedText(gallery.title ?: "").constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent() - 8.pixels()
            textScale = 1.5.pixels()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf this
        if (!gallery.description.isNullOrBlank()) UIWrappedText(gallery.description ?: "").constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent() - 8.pixels()
            color = Colors.TEXT_SECONDARY.toConstraint()
        } childOf this
    }
}