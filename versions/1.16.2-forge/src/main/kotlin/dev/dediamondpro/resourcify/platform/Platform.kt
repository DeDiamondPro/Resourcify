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

package dev.dediamondpro.resourcify.platform

import com.google.common.collect.Lists
import dev.dediamondpro.resourcify.mixins.AbstractResourcePackAccessor
import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.resources.I18n
import net.minecraft.resources.FilePack
import net.minecraft.resources.IResourcePack
import net.minecraft.resources.ResourcePackInfo
import net.minecraft.resources.ResourcePackList
import net.minecraft.util.SharedConstants
import net.minecraft.util.text.TranslationTextComponent
import java.io.File

object Platform {
    fun getMcVersion(): String {
        return SharedConstants.getVersion().name
    }

    fun getTranslateKey(screen: Screen): String {
        //#if MC < 11900
        val content = screen.title
        //#else
        //$$ val content = screen.title.content
        //#endif
        if (content !is TranslationTextComponent) {
            val optifineTranslation = I18n.format("of.options.shadersTitle")
            if (optifineTranslation != "of.options.shadersTitle" && optifineTranslation == screen.title.string) {
                return "of.options.shadersTitle"
            }
            return screen.title.string
        }
        return content.key
    }

    fun getSelectedResourcePacks(): List<File> {
        return UMinecraft.getMinecraft().resourcePackList.enabledPacks.mapNotNull {
            if (it.resourcePack !is FilePack) return@mapNotNull null
            getResourcePackFile(it.resourcePack)
        }
    }

    fun reloadResources() {
        UMinecraft.getMinecraft().reloadResources()
    }

    fun closeResourcePack(file: File): Int {
        val repo = UMinecraft.getMinecraft().resourcePackList
        repo.reloadPacksFromFinders()
        val packs = Lists.newArrayList(repo.enabledPacks)
        val pack = UMinecraft.getMinecraft().resourcePackList.allPacks.firstOrNull {
            if (it.resourcePack !is FilePack) return@firstOrNull false
            getResourcePackFile(it.resourcePack) == file
        }
        if (pack != null) {
            val index = packs.indexOf(pack)
            if (index != -1) {
                packs.remove(pack)
                //#if FORGE == 1 && MC == 11602
                repo.setEnabledPacks(packs.map { it.name })
                //#else
                //$$ repo.setEnabledProfiles(packs.map { it.name })
                //#endif
                applyResources(repo)
            }
            pack.resourcePack.close()
            return index
        }

        return -1
    }

    fun enableResourcePack(file: File, position: Int) {
        val repo = UMinecraft.getMinecraft().resourcePackList
        repo.reloadPacksFromFinders()
        val packs = Lists.newArrayList(repo.enabledPacks)
        packs.add(position, repo.allPacks.firstOrNull {
            if (it.resourcePack !is FilePack) return@firstOrNull false
            getResourcePackFile(it.resourcePack) == file
        } ?: return)
        //#if FORGE == 1 && MC == 11602
        repo.setEnabledPacks(packs.map { it.name })
        //#else
        //$$ repo.setEnabledProfiles(packs.map { it.name })
        //#endif
        applyResources(repo)
    }

    private fun applyResources(arg: ResourcePackList) {
        UMinecraft.getMinecraft().gameSettings.resourcePacks.clear()
        UMinecraft.getMinecraft().gameSettings.incompatibleResourcePacks.clear()
        val it: Iterator<*> = arg.enabledPacks.iterator()

        while (it.hasNext()) {
            val resourcePackInfo = it.next() as ResourcePackInfo
            if (!resourcePackInfo.isOrderLocked) {
                UMinecraft.getMinecraft().gameSettings.resourcePacks.add(resourcePackInfo.name)
                if (!resourcePackInfo.compatibility.isCompatible) {
                    UMinecraft.getMinecraft().gameSettings.incompatibleResourcePacks.add(resourcePackInfo.name)
                }
            }
        }
    }

    private fun getResourcePackFile(resourcePack: IResourcePack): File {
        //#if MC < 12002
        return (resourcePack as AbstractResourcePackAccessor).file
        //#else
        //$$ return (resourcePack as AbstractResourcePackAccessor).fileWrapper.file
        //#endif
    }

    fun saveSettings() {
        UMinecraft.getMinecraft().gameSettings.saveOptions()
    }
}