/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

package dev.dediamondpro.resourcify.gui.pack

import dev.dediamondpro.resourcify.elements.Icon
import dev.dediamondpro.resourcify.elements.MinecraftButton
import dev.dediamondpro.resourcify.gui.browsepage.BrowseScreen
import dev.dediamondpro.resourcify.gui.update.UpdateGui
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.util.Icons
import dev.dediamondpro.resourcify.util.isHidden
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UResolution
import gg.essential.universal.UScreen
import gg.essential.universal.USound
import java.io.File

object PackScreensAddition {
    private val window = Window(ElementaVersion.V5)

    private val addButton = MinecraftButton().constrain {
        x = CenterConstraint() + 194.pixels()
        y = 10.pixels()
        width = 20.pixels()
        height = 20.pixels()
    } childOf window
    private val addIcon = Icon(Icons.PLUS, true).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 16.pixels()
        height = 16.pixels()
    } childOf addButton
    private val updateButton = MinecraftButton().constrain {
        x = SiblingConstraint(8f, true)
        y = 10.pixels()
        width = 20.pixels()
        height = 20.pixels()
    } childOf window
    private val updateIcon = Icon(Icons.UPDATE, true).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 16.pixels()
        height = 16.pixels()
    } childOf updateButton

    fun onRender(matrix: UMatrixStack, type: ProjectType) {
        addButton.setX(basicXConstraint { type.plusX(UResolution.scaledWidth).toFloat() })
        addButton.setY(basicYConstraint { type.plusY(UResolution.scaledHeight).toFloat() })
        if (type.hasUpdateButton) updateButton.unhide()
        else updateButton.hide(true)
        updateButton.setX(basicXConstraint { type.updateX(UResolution.scaledWidth).toFloat() })
        updateButton.setY(basicYConstraint { type.updateY(UResolution.scaledHeight).toFloat() })
        window.draw(matrix)
    }

    fun onMouseClick(mouseX: Double, mouseY: Double, button: Int, type: ProjectType, folder: File) {
        if (addButton.isPointInside(mouseX.toFloat(), mouseY.toFloat()) && button == 0) {
            USound.playButtonPress()
            UScreen.displayScreen(BrowseScreen(type, folder))
        } else if (!updateButton.isHidden() && updateButton.isPointInside(mouseX.toFloat(), mouseY.toFloat())
            && button == 0
        ) {
            USound.playButtonPress()
            UScreen.displayScreen(UpdateGui(type, folder))
        }
    }
}