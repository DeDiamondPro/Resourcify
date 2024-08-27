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


import dev.dediamondpro.resourcify.gui.browsepage.BrowseScreen
import dev.dediamondpro.resourcify.gui.update.UpdateGui
import dev.dediamondpro.resourcify.mixins.PackScreenAccessor
import dev.dediamondpro.resourcify.services.ProjectType
import gg.essential.universal.UScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.util.ResourceLocation
import java.io.File

object PackScreensAddition {
    //#if MC < 12100
    private val plusImage = ResourceLocation("resourcify", "plus.png")
    private val updateImage = ResourceLocation("resourcify", "update.png")
    //#else
    //$$ private val plusImage = Identifier.of("resourcify", "plus.png")
    //$$ private val updateImage = Identifier.of("resourcify", "update.png")
    //#endif

    fun getButtons(screen: Screen, type: ProjectType): List<Button> {
        val folder = type.getDirectory(screen)
        val buttons = mutableListOf<Button>()
        val x = type.plusX(screen.width)
        val y = type.plusY(screen.height)
        buttons.add(ImageButton(x, y, plusImage) {
            UScreen.displayScreen(BrowseScreen(type, folder))
        })
        if (type.hasUpdateButton) buttons.add(ImageButton(x - 28, y, updateImage) {
            UScreen.displayScreen(UpdateGui(type, folder))
        })
        return buttons
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
    fun getDirectory(type: ProjectType, screen: Screen): File {
        return when (type) {
            //#if MC < 11904
            ProjectType.RESOURCE_PACK -> (screen as PackScreenAccessor).directory
            ProjectType.DATA_PACK -> (screen as PackScreenAccessor).directory
            //#else
            //$$ ProjectType.RESOURCE_PACK -> (screen as PackScreenAccessor).directory.toFile()
            //$$ ProjectType.DATA_PACK -> (screen as PackScreenAccessor).directory.toFile()
            //#endif
            ProjectType.IRIS_SHADER -> File("./shaderpacks")
            ProjectType.OPTIFINE_SHADER -> File("./shaderpacks")
            else -> error("Unknown project type: $type")
        }
    }
    //#endif
}