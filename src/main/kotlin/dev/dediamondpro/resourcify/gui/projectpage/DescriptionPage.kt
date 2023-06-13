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
import dev.dediamondpro.resourcify.util.DummyCache
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.markdown.MarkdownComponent
import java.awt.Color

class DescriptionPage(screen: ProjectScreen) : UIBlock(color = Color(0, 0, 0, 100)) {
    init {
        constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildLocationSizeConstraint() + 8.pixels()
        }
        MarkdownComponent(screen.project.get().body, disableSelection = true, imageCache = DummyCache).constrain {
            x = 6.pixels()
            y = 6.pixels()
            width = 100.percent() - 12.pixels()
        } childOf this
    }
}