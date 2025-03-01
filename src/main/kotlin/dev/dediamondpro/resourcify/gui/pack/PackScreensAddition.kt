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

package dev.dediamondpro.resourcify.gui.pack


import dev.dediamondpro.resourcify.gui.browsepage.BrowseScreen
import dev.dediamondpro.resourcify.gui.update.UpdateGui
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.gui.data.Icons
import gg.essential.universal.UScreen
import net.minecraft.client.gui.screens.Screen

object PackScreensAddition {
    fun getButtons(screen: Screen, type: ProjectType): List<ImageButton>? {
        if (!type.isEnabled()) {
            return null
        }
        val folder = type.getDirectory(screen)
        val buttons = mutableListOf<ImageButton>()
        buttons.add(ImageButton(screen, type.plusX, type.plusY, Icons.PLUS) {
            UScreen.displayScreen(BrowseScreen(type, folder))
        })
        if (type.hasUpdateButton(folder)) buttons.add(ImageButton(screen, type.updateX, type.updateY, Icons.UPDATE) {
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
            "selectWorld.title" -> ProjectType.WORLD
            else -> return null
        }
    }
}