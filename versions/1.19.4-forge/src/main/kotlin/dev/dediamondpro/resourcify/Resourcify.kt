/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License Version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dediamondpro.resourcify

//#if FORGE==1

import dev.dediamondpro.resourcify.events.EventHandler
import dev.dediamondpro.resourcify.updater.UpdateChecker
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent.ClientTickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod(ModInfo.ID)
object Resourcify {
    init {
        EventHandler
        UpdateChecker.startUpdateCheck()
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onTick(tickEvent: ClientTickEvent) {
        UpdateChecker.displayScreenIfNeeded()
        MinecraftForge.EVENT_BUS.unregister(this)
    }
}
//#endif