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

import dev.dediamondpro.resourcify.gui.projectpage.components.VersionCard
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels

class VersionsPage(screen: ProjectScreen) : UIContainer() {
    init {
        constrain {
            x = 0.pixels(alignOpposite = true)
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildBasedSizeConstraint()
        }
        for (version in screen.versions.get()) {
            VersionCard(version, screen.packHashes.get(), screen.downloadFolder).constrain {
                x = 0.pixels()
                y = SiblingConstraint(padding = 4f)
            } childOf this
        }
    }
}