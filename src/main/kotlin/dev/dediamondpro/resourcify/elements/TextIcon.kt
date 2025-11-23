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

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import java.awt.Color

//? if <1.21.11 {
/*import net.minecraft.resources.ResourceLocation
*///?} else
import net.minecraft.resources.Identifier

class TextIcon(
    text: String,
    asset: /*? if <1.21.11 {*/ /*ResourceLocation *//*?} else {*/Identifier /*?}*/,
    shadow: Boolean = true,
    color: ColorConstraint = Color.WHITE.toConstraint()
) : UIContainer() {
    init {
        UIText(text, shadow).constrain {
            y = CenterConstraint()
            this.color = color
        } childOf this
        Icon(asset, shadow, color).constrain {
            x = SiblingConstraint(padding = 2f)
            width = 9.pixels()
            height = 9.pixels()
        } childOf this
    }
}