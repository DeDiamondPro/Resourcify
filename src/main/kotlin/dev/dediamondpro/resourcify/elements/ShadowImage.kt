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

package dev.dediamondpro.resourcify.elements

import dev.dediamondpro.resourcify.util.EmptyImage
import dev.dediamondpro.resourcify.util.Utils
import dev.dediamondpro.resourcify.util.supplyAsync
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.dsl.*
import java.awt.Color
import javax.imageio.ImageIO

class ShadowImage(
    asset: String,
    imageColor: ColorConstraint = Color.WHITE.toConstraint(),
    shadowColor: ColorConstraint = ShadowColorConstraint(imageColor),
) : UIContainer() {
    init {
        UIImage(
            supplyAsync { ImageIO.read(this::class.java.getResourceAsStream(asset)) },
            EmptyImage,
            EmptyImage
        ).constrain {
            x = 1.pixels()
            y = 1.pixels()
            width = 100.percent()
            height = 100.percent()
            color = shadowColor
        } childOf this
        UIImage(
            supplyAsync { ImageIO.read(this::class.java.getResourceAsStream(asset)) },
            EmptyImage,
            EmptyImage
        ).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 100.percent()
            color = imageColor
        } childOf this
    }

    private class ShadowColorConstraint(val normalColor: ColorConstraint) : ColorConstraint {
        override var cachedValue: Color = Color.WHITE
        override var constrainTo: UIComponent? = null
        override var recalculate: Boolean = true

        override fun getColorImpl(component: UIComponent): Color {
            return Utils.getShadowColor(normalColor.getColor(component))
        }

        override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}
    }
}