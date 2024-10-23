/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

package dev.dediamondpro.resourcify.elements

import dev.dediamondpro.resourcify.util.EmptyImage
import dev.dediamondpro.resourcify.util.supply
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.dsl.*
import java.awt.Color
import javax.imageio.ImageIO

class Icon(icon: String, shadow: Boolean, color: ColorConstraint) : UIContainer() {
    constructor(icon: String, shadow: Boolean, color: Color = Color.WHITE) : this(icon, shadow, color.toConstraint())

    init {
        (if (shadow) ShadowImage(icon, color)
        else UIImage(
            supply { ImageIO.read(this::class.java.getResourceAsStream(icon)) },
            EmptyImage, EmptyImage
        )).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 100.percent()
            this.color = color
        } childOf this
    }
}