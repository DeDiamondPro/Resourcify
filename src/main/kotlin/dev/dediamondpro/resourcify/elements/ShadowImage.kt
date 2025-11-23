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

package dev.dediamondpro.resourcify.elements

import dev.dediamondpro.resourcify.util.Utils
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.dsl.*
import java.awt.Color

//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation
*///?} else
import net.minecraft.resources.Identifier

class ShadowImage(
    asset: /*? if <1.21.11 {*/ /*ResourceLocation *//*?} else {*/Identifier /*?}*/,
    imageColor: ColorConstraint = Color.WHITE.toConstraint(),
    shadowColor: ColorConstraint = ShadowColorConstraint(imageColor),
) : UIContainer() {
    init {
        McImage(asset).constrain {
            x = 1.pixels()
            y = 1.pixels()
            width = 100.percent()
            height = 100.percent()
            color = shadowColor
        } childOf this
        McImage(asset).constrain {
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