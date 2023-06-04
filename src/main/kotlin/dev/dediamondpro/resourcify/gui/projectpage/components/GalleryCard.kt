/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.gui.projectpage.components

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.constraints.ImageFillConstraint
import dev.dediamondpro.resourcify.modrinth.GalleryResponse
import dev.dediamondpro.resourcify.util.ofURL
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UKeyboard
import java.awt.Color

class GalleryCard(gallery: GalleryResponse) : UIBlock(color = Color(0, 0, 0, 100)) {
    init {
        constrain {
            x = 0.pixels()
            y = 0.pixels()
            height = ChildLocationSizeConstraint()
        }
        onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            val background = UIBlock(color = Color(0, 0, 0, 150)).constrain {
                x = 0.pixels()
                y = 0.pixels()
                width = 100.percent()
                height = 100.percent()
            }.onFocusLost {
                setFloating(false)
                Window.of(this).removeChild(this)
            }.onKeyType { _, keyCode ->
                if (keyCode != UKeyboard.KEY_ESCAPE) return@onKeyType
                releaseWindowFocus()
                setFloating(false)
                Window.of(this).removeChild(this)
            } childOf Window.of(this)
            background.setFloating(true)
            background.grabWindowFocus()
            UIImage.ofURL(gallery.url, false).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                width = ImageFillConstraint()
                height = ImageFillConstraint()
            } childOf background
        }
        UIImage.ofURL(gallery.url, false).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = ImageAspectConstraint()
        } childOf this
        if (gallery.title != null) UIWrappedText(gallery.title).constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent() - 8.pixels()
            textScale = 1.5.pixels()
        } childOf this
        if (gallery.description != null) UIWrappedText(gallery.description).constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent() - 8.pixels()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf this
    }
}