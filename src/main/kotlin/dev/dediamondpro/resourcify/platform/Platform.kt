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
import net.minecraft.client.resource.language.I18n
import net.minecraft.resource.ZipResourcePack
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.resource.ResourcePackManager
import net.minecraft.SharedConstants
import net.minecraft.text.TranslatableTextContent
import java.io.File

object Platform {
    fun getMcVersion(): String {
        return SharedConstants.getGameVersion().name
    }

    fun getTranslateKey(screen: Screen): String {
        //#if MC < 11900
        //$$ val content = screen.title
        //#else
        val content = screen.title.content
        //#endif
        if (content !is TranslatableTextContent) {
            val optifineTranslation = I18n.translate("of.options.shadersTitle")
            if (optifineTranslation != "of.options.shadersTitle" && optifineTranslation == screen.title.string) {
                return "of.options.shadersTitle"
            }
            return screen.title.string
        }
        return content.key
    }

    fun getSelectedResourcePacks(): List<File> {
        return UMinecraft.getMinecraft().resourcePackManager.enabledProfiles.mapNotNull {
            if (it.createResourcePack() !is ZipResourcePack) return@mapNotNull null
            getResourcePackFile(it.createResourcePack())
        }
    }

    fun reloadResources() {
        UMinecraft.getMinecraft().reloadResources()
    }

    fun closeResourcePack(file: File): Int {
        val repo = UMinecraft.getMinecraft().resourcePackManager
        repo.scanPacks()
        val packs = Lists.newArrayList(repo.enabledProfiles)
        val pack = UMinecraft.getMinecraft().resourcePackManager.profiles.firstOrNull {
            if (it.createResourcePack() !is ZipResourcePack) return@firstOrNull false
            getResourcePackFile(it.createResourcePack()) == file
        }
        if (pack != null) {
            val index = packs.indexOf(pack)
            if (index != -1) {
                packs.remove(pack)
                //#if FORGE == 1 && MC == 11602
                //$$ repo.setEnabledPacks(packs.map { it.name })
                //#else
                repo.setEnabledProfiles(packs.map { it.id })
                //#endif
                applyResources(repo)
            }
            pack.createResourcePack().close()
            return index
        }

        return -1
    }

    fun enableResourcePack(file: File, position: Int) {
        val repo = UMinecraft.getMinecraft().resourcePackManager
        repo.scanPacks()
        val packs = Lists.newArrayList(repo.enabledProfiles)
        packs.add(position, repo.profiles.firstOrNull {
            if (it.createResourcePack() !is ZipResourcePack) return@firstOrNull false
            getResourcePackFile(it.createResourcePack()) == file
        } ?: return)
        //#if FORGE == 1 && MC == 11602
        //$$ repo.setEnabledPacks(packs.map { it.name })
        //#else
        repo.setEnabledProfiles(packs.map { it.id })
        //#endif
        applyResources(repo)
    }

    private fun applyResources(arg: ResourcePackManager) {
        UMinecraft.getMinecraft().options.resourcePacks.clear()
        UMinecraft.getMinecraft().options.incompatibleResourcePacks.clear()
        val it: Iterator<*> = arg.enabledProfiles.iterator()

        while (it.hasNext()) {
            val resourcePackInfo = it.next() as ResourcePackProfile
            if (!resourcePackInfo.isPinned) {
                UMinecraft.getMinecraft().options.resourcePacks.add(resourcePackInfo.id)
                if (!resourcePackInfo.compatibility.isCompatible) {
                    UMinecraft.getMinecraft().options.incompatibleResourcePacks.add(resourcePackInfo.id)
                }
            }
        }
    }

    private fun getResourcePackFile(resourcePack: ResourcePack): File {
        //#if MC < 12002
        //$$ return (resourcePack as AbstractResourcePackAccessor).file
        //#else
        return (resourcePack as AbstractResourcePackAccessor).fileWrapper.file
        //#endif
    }

    fun saveSettings() {
        UMinecraft.getMinecraft().options.write()
    }
}