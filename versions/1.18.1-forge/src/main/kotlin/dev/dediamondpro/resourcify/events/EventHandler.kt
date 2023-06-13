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

package dev.dediamondpro.resourcify.events

import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition
import dev.dediamondpro.resourcify.mixins.PackScreenAccessor
import net.minecraft.client.gui.screens.packs.PackSelectionScreen
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.client.event.ScreenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent

object EventHandler {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onMouseClicked(event: ScreenEvent.MouseClickedEvent.Pre) {
        if (event.screen !is PackSelectionScreen) return
        val type = ResourcePackAddition.getType((event.screen.title as TranslatableComponent).key) ?: return
        ResourcePackAddition.onMouseClick(
            event.mouseX, event.mouseY,
            event.button, type,
            (event.screen as PackScreenAccessor).directory
        )
    }
}