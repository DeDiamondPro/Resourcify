/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
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