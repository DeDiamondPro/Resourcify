/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.elements

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels

class TextIcon(text: String, asset: String, shadow: Boolean = true) : UIContainer() {
    init {
        UIText(text, shadow).constrain {
            y = CenterConstraint()
        } childOf this
        Icon(asset, shadow).constrain {
            x = SiblingConstraint(padding = 2f)
            width = 9.pixels()
            height = 9.pixels()
        } childOf this
    }
}