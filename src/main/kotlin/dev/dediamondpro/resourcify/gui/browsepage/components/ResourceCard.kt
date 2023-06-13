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

package dev.dediamondpro.resourcify.gui.browsepage.components

import dev.dediamondpro.resourcify.constraints.ImageFillConstraint
import dev.dediamondpro.resourcify.gui.projectpage.ProjectScreen
import dev.dediamondpro.resourcify.modrinth.ApiInfo
import dev.dediamondpro.resourcify.modrinth.ProjectObject
import dev.dediamondpro.resourcify.util.ofURL
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.UScreen
import java.awt.Color
import java.io.File

class ResourceCard(project: ProjectObject, type: ApiInfo.ProjectType, downloadFolder: File) : UIBlock(color = Color(0, 0, 0, 100)) {

    init {
        constrain {
            x = 0.pixels()
            y = 0.pixels()
        }.onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            UScreen.displayScreen(ProjectScreen(project, type, downloadFolder))
        }

        val bannerHolder = UIBlock(color = project.color?.let { Color(it) } ?: Color.BLACK).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 84.pixels()
        } effect ScissorEffect() childOf this

        val bannerUrl = project.featuredGallery ?: project.gallery.firstOrNull()
        if (bannerUrl != null) UIImage.ofURL(bannerUrl, false).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = ImageFillConstraint(ImageFillConstraint.FillType.CROP)
            height = ImageFillConstraint(ImageFillConstraint.FillType.CROP)
        } childOf bannerHolder

        val imageHolder = UIBlock(color = Color(0, 0, 0, 150)).constrain {
            x = 4.pixels()
            y = 62.pixels()

            width = 56.pixels()
            height = 56.pixels()
        } childOf this

        if (project.iconUrl.isNullOrBlank()) {
            UIImage.ofResource("/pack.png")
        } else {
            UIImage.ofURL(project.iconUrl)
        }.constrain {
            width = 100.percent()
            height = 100.percent()
        } childOf imageHolder

        val titleHolder = UIContainer().constrain {
            x = 64.pixels()
            y = 90.pixels()
            width = 100.percent() - 68.pixels()
            height = ChildBasedSizeConstraint(padding = 2f)
        } effect ScissorEffect() childOf this

        UIText(project.title).constrain {
            textScale = 1.5f.pixels()
        } childOf titleHolder
        UIText("by ${project.author}").constrain {
            y = SiblingConstraint(padding = 2f)
            textScale = 1.2f.pixels()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf titleHolder

        UIWrappedText(project.description).constrain {
            x = 4.pixels()
            y = 122.pixels()
            width = 100.percent() - 8.pixels()
        } effect ScissorEffect() childOf this
    }
}