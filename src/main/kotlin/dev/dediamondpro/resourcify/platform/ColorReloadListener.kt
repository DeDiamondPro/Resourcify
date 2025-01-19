/*
 * This file is part of Resourcify
 * Copyright (C) 2025 DeDiamondPro
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

import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.util.Utils
import dev.dediamondpro.resourcify.util.fromJson
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller

//? if fabric
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener

class ColorReloadListener : SimplePreparableReloadListener<Map<String, Colors.JsonColor>>()
    //? if fabric
    ,IdentifiableResourceReloadListener
{
    private val colorsLocation = Utils.createResourceLocation("colors.json")

    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller): Map<String, Colors.JsonColor> {
        try {
            return resourceManager.openAsReader(colorsLocation).use { it.fromJson() }
        } catch (e: Exception) {
            println("Failed to read Resourcify's colors file.")
            e.printStackTrace()
            return emptyMap()
        }
    }

    override fun apply(colors: Map<String, Colors.JsonColor>, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        Colors.load(colors)
    }

    //? if fabric {
    override fun getFabricId(): ResourceLocation {
        return Utils.createResourceLocation("color-reload-listener")
    }
    //?}
}