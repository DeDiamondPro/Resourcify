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

package dev.dediamondpro.resourcify.gui.browsepage.components

import dev.dediamondpro.resourcify.constraints.ImageFillConstraint
import dev.dediamondpro.resourcify.elements.McImage
import dev.dediamondpro.resourcify.gui.projectpage.ProjectScreen
import dev.dediamondpro.resourcify.services.IProject
import dev.dediamondpro.resourcify.services.IService
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.util.Icons
import dev.dediamondpro.resourcify.util.localize
import dev.dediamondpro.resourcify.util.ofResourceCustom
import dev.dediamondpro.resourcify.util.ofURLCustom
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.UScreen
import java.awt.Color
import java.io.File

class ResourceCard(service: IService, project: IProject, type: ProjectType, downloadFolder: File) :
    UIBlock(color = Color(0, 0, 0, 100)) {

    init {
        constrain {
            x = 0.pixels()
            y = 0.pixels()
        }.onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            UScreen.displayScreen(ProjectScreen(service, project, type, downloadFolder))
        }

        val bannerColor = project.getBannerColor() ?: Color(
            (200 * Math.random()).toInt(),
            (200 * Math.random()).toInt(),
            (200 * Math.random()).toInt()
        )
        val bannerHolder = UIBlock(color = bannerColor).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 84.pixels()
        } effect ScissorEffect() childOf this

        project.getBannerUrl()?.let {
            UIImage.ofURLCustom(it, false)
                .constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    width = ImageFillConstraint(ImageFillConstraint.FillType.CROP)
                    height = ImageFillConstraint(ImageFillConstraint.FillType.CROP)
                } childOf bannerHolder
        }

        val imageHolder = UIBlock(color = Color(0, 0, 0, 150)).constrain {
            x = 4.pixels()
            y = 62.pixels()

            width = 56.pixels()
            height = 56.pixels()
        } childOf this

        val iconUrl = project.getIconUrl()
        if (iconUrl == null) {
            McImage(Icons.DEFAULT_ICON)
        } else {
            UIImage.ofURLCustom(iconUrl)
        }.constrain {
            width = ImageFillConstraint(ImageFillConstraint.FillType.CROP)
            height = ImageFillConstraint(ImageFillConstraint.FillType.CROP)
        } childOf imageHolder

        val titleHolder = UIContainer().constrain {
            x = 64.pixels()
            y = 90.pixels()
            width = 100.percent() - 68.pixels()
            height = ChildBasedSizeConstraint(padding = 2f)
        } effect ScissorEffect() childOf this

        UIText(project.getName()).constrain {
            textScale = 1.5f.pixels()
        } childOf titleHolder
        UIText("resourcify.browse.by".localize(project.getAuthor())).constrain {
            y = SiblingConstraint(padding = 2f)
            textScale = 1.2f.pixels()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf titleHolder

        UIWrappedText(project.getSummary()).constrain {
            x = 4.pixels()
            y = 122.pixels()
            width = 100.percent() - 8.pixels()
        } effect ScissorEffect() childOf this
    }
}