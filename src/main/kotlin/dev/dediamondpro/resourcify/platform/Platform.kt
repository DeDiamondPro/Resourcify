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

package dev.dediamondpro.resourcify.platform

import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.server.packs.FilePackResources
import net.minecraft.server.packs.repository.Pack
import java.io.File

object Platform {
    fun getMcVersion(): String {
        return SharedConstants.getCurrentVersion().name
    }

    fun getLoader(): String {
        //? if fabric {
        return "fabric"
        //?} else if forge {
        /*return "forge"
        *///?} else if neoforge {
        /*return "neoforge"
        *///?}
    }

    fun getTranslateKey(screen: Screen): String {
        val content = screen.title.contents
        if (content !is TranslatableContents) {
            val optifineTranslation = I18n.get("of.options.shadersTitle")
            if (optifineTranslation != "of.options.shadersTitle" && optifineTranslation == screen.title.string) {
                return "of.options.shadersTitle"
            }
            return screen.title.string
        }
        return content.key
    }

    fun getFileFromPackResourceSupplier(resources: Pack.ResourcesSupplier): File? {
        //? if <1.21 {
        /*resources.open("").use { pack ->
            if (pack !is FilePackResources) {
                return null
            }
            return pack.file
        }
        *///?} else {
        if (resources !is FilePackResources.FileResourcesSupplier) {
            return null
        }
        return resources.content
        //?}
    }

    fun getFileInGameDir(name: String): File {
        return File(Minecraft.getInstance().gameDirectory, name)
    }
}