/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
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