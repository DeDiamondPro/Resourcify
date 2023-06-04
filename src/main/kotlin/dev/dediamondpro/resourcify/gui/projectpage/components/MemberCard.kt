/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.gui.projectpage.components

import dev.dediamondpro.resourcify.modrinth.Member
import dev.dediamondpro.resourcify.util.capitalizeAll
import dev.dediamondpro.resourcify.util.ofURL
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

class MemberCard(member: Member) : UIContainer() {

    init {
        constrain {
            height = 32.pixels()
        }
        onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            UDesktop.browse(URI("https://modrinth.com/user/${member.user.username}"))
        }
        UIImage.ofURL(member.user.avatarUrl).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 32.pixels()
            height = 32.pixels()
        } childOf this
        val textHolder = UIContainer().constrain {
            x = 36.pixels()
            y = 7.pixels()
            width = 100.percent() - 36.pixels()
            height = ChildBasedSizeConstraint(padding = 2f)
        } effect ScissorEffect() childOf this
        UIText("${ChatColor.BOLD}${member.user.username}").constrain {
            x = 0.pixels()
            y = 0.pixels()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf textHolder
        UIText(member.role.capitalizeAll()).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 2f)
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf textHolder
    }
}