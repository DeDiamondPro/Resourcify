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

import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.util.Icons
import dev.dediamondpro.resourcify.util.localize
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.basicColorConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UScreen
import java.awt.Color

class Paginator(screen: PaginatedScreen) : UIBlock(color = Color(0, 0, 0, 100)) {
    init {
        Icon(Icons.BACK, true).constrain {
            x = 8.pixels()
            y = CenterConstraint()
            width = 9.pixels()
            height = 9.pixels()
        }.onMouseClick { screen.goBack() } childOf this
        UIText("resourcify.screens.close".localize()).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        }.onMouseClick {
            UScreen.displayScreen(PaginatedScreen.backScreens.findLast { it !is PaginatedScreen })
            PaginatedScreen.cleanUp()
        } childOf this
        Icon(Icons.FORWARD, true, basicColorConstraint {
            if (PaginatedScreen.forwardScreens.isEmpty()) Color.LIGHT_GRAY else Color.WHITE
        }).constrain {
            x = 8.pixels(true)
            y = CenterConstraint()
            width = 9.pixels()
            height = 9.pixels()
        }.onMouseClick { screen.goForward() } childOf this
    }
}