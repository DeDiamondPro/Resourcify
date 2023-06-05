/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.events

import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition
import net.minecraft.client.gui.screens.packs.PackSelectionScreen
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent

object EventHandler {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onMouseClicked(event: GuiScreenEvent.MouseClickedEvent.Pre) {
        if (event.gui !is PackSelectionScreen) return
        val title = (event.gui.title as TranslatableComponent).key
        if (title != "resourcePack.title") return
        ResourcePackAddition.onMouseClick(event.mouseX, event.mouseY, event.button)
    }
}