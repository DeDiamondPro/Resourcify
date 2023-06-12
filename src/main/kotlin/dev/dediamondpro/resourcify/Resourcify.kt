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

package dev.dediamondpro.resourcify

import dev.dediamondpro.resourcify.updater.UpdateChecker
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@Mod(
    name = ModInfo.NAME,
    modid = ModInfo.ID,
    version = ModInfo.VERSION,
    modLanguageAdapter = "dev.dediamondpro.resourcify.platform.KotlinLanguageAdapter"
)
object Resourcify {

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent) {
        UpdateChecker.startUpdateCheck()
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        UpdateChecker.displayScreenIfNeeded()
        MinecraftForge.EVENT_BUS.unregister(this)
    }
}