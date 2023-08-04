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
            (it.resourcePack as AbstractResourcePackAccessor).file
        }
    }

    fun reloadResources() {
        UMinecraft.getMinecraft().reloadResources()
    }

    fun closeResourcePack(pack: File) {
        UMinecraft.getMinecraft().resourcePackList.allPacks.firstOrNull {
            if (it.resourcePack !is FilePack) return@firstOrNull false
            (it.resourcePack as AbstractResourcePackAccessor).file == pack
        }?.resourcePack?.close()
    }

    fun replaceResourcePack(oldPack: File, newPack: File) {
        val repo = UMinecraft.getMinecraft().resourcePackList
        repo.reloadPacksFromFinders()
        val packs = Lists.newArrayList(repo.enabledPacks)
        val old = repo.allPacks.firstOrNull {
            if (it.resourcePack !is FilePack) return@firstOrNull false
            (it.resourcePack as AbstractResourcePackAccessor).file == oldPack
        }
        old?.let {
            packs.remove(it)
            it.resourcePack.close()
        }
        println(repo.allPacks.firstOrNull {
            if (it.resourcePack !is FilePack) return@firstOrNull false
            (it.resourcePack as AbstractResourcePackAccessor).file == newPack
        })
        packs.add(repo.allPacks.firstOrNull {
            if (it.resourcePack !is FilePack) return@firstOrNull false
            (it.resourcePack as AbstractResourcePackAccessor).file == newPack
        } ?: return)
        //#if FORGE == 1 && MC == 11602
        repo.setEnabledPacks(packs.map { it.name })
        //#else
        //$$ repo.setEnabledProfiles(packs.map { it.name })
        //#endif

        applyResources(repo)
        UMinecraft.getMinecraft().gameSettings.saveOptions()
    }

    private fun applyResources(arg: ResourcePackList) {
        UMinecraft.getMinecraft().gameSettings.resourcePacks.clear()
        UMinecraft.getMinecraft().gameSettings.incompatibleResourcePacks.clear()
        val var3: Iterator<*> = arg.enabledPacks.iterator()

        while (var3.hasNext()) {
            val resourcePackInfo = var3.next() as ResourcePackInfo
            if (!resourcePackInfo.isOrderLocked) {
                UMinecraft.getMinecraft().gameSettings.resourcePacks.add(resourcePackInfo.name)
                if (!resourcePackInfo.compatibility.isCompatible) {
                    UMinecraft.getMinecraft().gameSettings.incompatibleResourcePacks.add(resourcePackInfo.name)
                }
            }
        }
    }
}