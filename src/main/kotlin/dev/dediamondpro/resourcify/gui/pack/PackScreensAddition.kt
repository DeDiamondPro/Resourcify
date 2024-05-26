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
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UScreen
import gg.essential.universal.USound
import java.io.File

//#if MC >= 11600
//$$ import net.minecraft.util.text.TranslationTextComponent
//$$ import dev.dediamondpro.resourcify.mixins.PackScreenAccessor
//$$ import net.minecraft.client.gui.screen.Screen
//#endif

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
        addButton.setX(type.plusX)
        addButton.setY(type.plusY)
        if (type.hasUpdateButton) updateButton.unhide()
        else updateButton.hide(true)
        updateButton.setY(type.plusY)
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

    fun getType(title: String): ProjectType? {
        return when (title) {
            "resourcePack.title" -> ProjectType.RESOURCE_PACK
            "dataPack.title" -> ProjectType.DATA_PACK
            "of.options.shadersTitle" -> ProjectType.OPTIFINE_SHADER
            "options.iris.shaderPackSelection.title" -> ProjectType.IRIS_SHADER
            else -> return null
        }
    }

    //#if MC >= 11600
    //$$ fun getDirectory(type: ProjectType, screen: Screen): File {
    //$$     return when(type) {
    //$$         //#if MC < 11904
    //$$         ProjectType.RESOURCE_PACK -> (screen as PackScreenAccessor).directory
    //$$         ProjectType.DATA_PACK -> (screen as PackScreenAccessor).directory
    //$$         //#else
    //$$         //$$ ProjectType.RESOURCE_PACK -> (screen as PackScreenAccessor).directory.toFile()
    //$$         //$$ ProjectType.DATA_PACK -> (screen as PackScreenAccessor).directory.toFile()
    //$$         //#endif
    //$$         ProjectType.IRIS_SHADER -> File("./shaderpacks")
    //$$         ProjectType.OPTIFINE_SHADER -> File("./shaderpacks")
    //$$         else -> error("Unknown project type: $type")
    //$$     }
    //$$ }
    //#endif
}