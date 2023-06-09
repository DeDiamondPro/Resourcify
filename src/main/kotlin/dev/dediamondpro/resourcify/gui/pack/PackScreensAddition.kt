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
import dev.dediamondpro.resourcify.modrinth.ApiInfo
import dev.dediamondpro.resourcify.util.Icons
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UScreen
import java.io.File
//#if MC >= 11600
//$$ import net.minecraft.util.text.TranslationTextComponent
//$$ import dev.dediamondpro.resourcify.mixins.PackScreenAccessor
//$$ import net.minecraft.client.gui.screen.Screen
//#endif

object PackScreensAddition {
    private val window = Window(ElementaVersion.V2)

    private val button = MinecraftButton().constrain {
        x = CenterConstraint() + 194.pixels()
        y = 10.pixels()
        width = 20.pixels()
        height = 20.pixels()
    } childOf window
    private val icon = Icon(Icons.PLUS, true).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = 16.pixels()
        height = 16.pixels()
    } childOf button

    fun onRender(matrix: UMatrixStack, type: ApiInfo.ProjectType) {
        button.setX(type.plusX)
        button.setY(type.plusY)
        window.draw(matrix)
    }

    fun onMouseClick(mouseX: Double, mouseY: Double, button: Int, type: ApiInfo.ProjectType, folder: File) {
        if (!this.button.isPointInside(mouseX.toFloat(), mouseY.toFloat()) || button != 0) return
        UScreen.displayScreen(BrowseScreen(type, folder))
    }

    fun getType(title: String): ApiInfo.ProjectType? {
        return when (title) {
            "resourcePack.title" -> ApiInfo.ProjectType.RESOURCE_PACK
            "dataPack.title" -> ApiInfo.ProjectType.DATA_PACK
            "of.options.shadersTitle" -> ApiInfo.ProjectType.OPTIFINE_SHADER
            "options.iris.shaderPackSelection.title" -> ApiInfo.ProjectType.IRIS_SHADER
            else -> return null
        }
    }

    //#if MC >= 11600
    //$$ fun getDirectory(type: ApiInfo.ProjectType, screen: Screen): File {
    //$$     return when(type) {
    //$$         //#if MC < 11904
    //$$         ApiInfo.ProjectType.RESOURCE_PACK -> (screen as PackScreenAccessor).directory
    //$$         ApiInfo.ProjectType.DATA_PACK -> (screen as PackScreenAccessor).directory
    //$$         //#else
    //$$         //$$ ApiInfo.ProjectType.RESOURCE_PACK -> (screen as PackScreenAccessor).directory.toFile()
    //$$         //$$ ApiInfo.ProjectType.DATA_PACK -> (screen as PackScreenAccessor).directory.toFile()
    //$$         //#endif
    //$$         ApiInfo.ProjectType.IRIS_SHADER -> File("./shaderpacks")
    //$$         ApiInfo.ProjectType.OPTIFINE_SHADER -> File("./shaderpacks")
    //$$     }
    //$$ }
    //#endif
}