/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
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

package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.gui.projectpage.components.GalleryCard
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import kotlin.math.ceil

class GalleryPage(screen: ProjectScreen) : UIContainer() {
    init {
        constrain {
            x = 0.pixels(alignOpposite = true)
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        }

        screen.project.getGalleryImages().thenAccept { gallery ->
            Window.enqueueRenderOperation {
                for (i in 0 until ceil(gallery.size / 2f).toInt()) {
                    val row = UIContainer().constrain {
                        x = 0.pixels()
                        y = SiblingConstraint(padding = 4f)
                        width = 100.percent()
                        height = ChildBasedMaxSizeConstraint()
                    } childOf this
                    GalleryCard(gallery[i * 2]).constrain {
                        x = 0.pixels()
                        y = 0.pixels()
                        width = 50.percent() - 2.pixels()
                    } childOf row
                    if (gallery.size > i * 2 + 1) GalleryCard(gallery[i * 2 + 1]).constrain {
                        x = 0.pixels(true)
                        y = 0.pixels()
                        width = 50.percent() - 2.pixels()
                    } childOf row
                }
            }
        }
    }
}