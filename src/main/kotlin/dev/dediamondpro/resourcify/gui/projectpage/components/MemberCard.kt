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

package dev.dediamondpro.resourcify.gui.projectpage.components

import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.services.IMember
import dev.dediamondpro.resourcify.util.capitalizeAll
import dev.dediamondpro.resourcify.util.ofURLCustom
import dev.dediamondpro.resourcify.util.toURL
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.ChatColor
import gg.essential.universal.UDesktop
import java.awt.Color
import java.net.URI

class MemberCard(member: IMember) : UIContainer() {

    init {
        constrain {
            height = 32.pixels()
        }
        onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            UDesktop.browse(URI(member.url))
        }
        member.avatarUrl?.toURL()?.let {
            UIImage.ofURLCustom(it).constrain {
                x = 0.pixels()
                y = 0.pixels()
                width = 32.pixels()
                height = 32.pixels()
            } childOf this
        }
        val textHolder = UIContainer().constrain {
            x = SiblingConstraint(padding = 4f)
            y = 7.pixels()
            width = 100.percent() - 36.pixels()
            height = ChildBasedSizeConstraint(padding = 2f)
        } effect ScissorEffect() childOf this
        UIText("${ChatColor.BOLD}${member.name}").constrain {
            x = 0.pixels()
            y = 0.pixels()
            color = Colors.SECONDARY.toConstraint()
        } childOf textHolder
        UIText(member.role.capitalizeAll()).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 2f)
            color = Colors.SECONDARY.toConstraint()
        } childOf textHolder
    }
}