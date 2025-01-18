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

import com.google.common.collect.Lists
import dev.dediamondpro.resourcify.mixins.AbstractResourcePackAccessor
import gg.essential.universal.UMinecraft
import net.minecraft.SharedConstants
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.server.packs.FilePackResources
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackRepository
import java.io.File

object Platform {
    fun getMcVersion(): String {
        return SharedConstants.getCurrentVersion().name
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

    fun getSelectedResourcePacks(): List<File> {
        return UMinecraft.getMinecraft().resourcePackRepository.selectedPacks.mapNotNull {
            val pack = it.open()
            val result = if (pack is FilePackResources) getResourcePackFile(pack) else null
            pack.close()
            return@mapNotNull result
        }
    }

    fun reloadResources() {
        UMinecraft.getMinecraft().reloadResourcePacks()
    }

    fun closeResourcePack(file: File): Int {
        val repo = UMinecraft.getMinecraft().resourcePackRepository
        repo.reload()
        val packs = Lists.newArrayList(repo.selectedPacks)
        val pack = repo.availablePacks.firstOrNull {
            val pack = it.open()
            val result = pack is FilePackResources && getResourcePackFile(pack) == file
            pack.close()
            return@firstOrNull result
        }
        if (pack != null) {
            val index = packs.indexOf(pack)
            if (index != -1) {
                packs.remove(pack)
                repo.setSelected(packs.map { it.id })
                applyResources(repo)
            }
            return index
        }

        return -1
    }

    fun enableResourcePack(file: File, position: Int) {
        val repo = UMinecraft.getMinecraft().resourcePackRepository
        repo.reload()
        val packs = Lists.newArrayList(repo.selectedPacks)
        packs.add(position, repo.availablePacks.firstOrNull {
            val pack = it.open()
            val result = pack is FilePackResources && getResourcePackFile(pack) == file
            pack.close()
            return@firstOrNull result
        } ?: return)
        repo.setSelected(packs.map { it.id })
        applyResources(repo)
    }

    private fun applyResources(arg: PackRepository) {
        UMinecraft.getMinecraft().options.resourcePacks.clear()
        UMinecraft.getMinecraft().options.incompatibleResourcePacks.clear()
        val it: Iterator<*> = arg.selectedPacks.iterator()

        while (it.hasNext()) {
            val resourcePackInfo = it.next() as Pack
            if (!resourcePackInfo.isFixedPosition) {
                UMinecraft.getMinecraft().options.resourcePacks.add(resourcePackInfo.id)
                if (!resourcePackInfo.compatibility.isCompatible) {
                    UMinecraft.getMinecraft().options.incompatibleResourcePacks.add(resourcePackInfo.id)
                }
            }
        }
    }

    private fun getResourcePackFile(resourcePack: FilePackResources): File {
        //? >=1.21 {
         return (resourcePack as AbstractResourcePackAccessor).fileWrapper.file
        //?} else
        /*return (resourcePack as AbstractResourcePackAccessor).file*/
    }

    fun saveSettings() {
        UMinecraft.getMinecraft().options.save()
    }
}