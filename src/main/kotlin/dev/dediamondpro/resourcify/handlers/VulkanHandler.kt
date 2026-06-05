/*
 * This file is part of Resourcify
 * Copyright (C) 2026 DeDiamondPro
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

package dev.dediamondpro.resourcify.handlers

import gg.essential.universal.UScreen
import net.minecraft.client.gui.screens.Screen

//?if >=26.2 {
import com.mojang.blaze3d.vulkan.VulkanBackend
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.AlertScreen
import net.minecraft.network.chat.Component

//?}

object VulkanHandler {
    inline fun runOrBlock(callable: () -> Unit) {
        //?if >=26.2 {
        val mc = Minecraft.getInstance()
        if (mc.window.backend() is VulkanBackend) {
            val currentScreen = mc.gui.screen()
            mc.setScreenAndShow(
                AlertScreen(
                    { UScreen.displayScreen(currentScreen) },
                    Component.translatable("resourcify.no-vulkan.title"),
                    Component.translatable("resourcify.no-vulkan.description")
                )
            )
            return
        }
        //?}
        callable.invoke()
    }

    inline fun createOrBlock(callable: () -> Screen): Screen {
        //?if >=26.2 {
        val mc = Minecraft.getInstance()
        if (mc.window.backend() is VulkanBackend) {
            val currentScreen = mc.gui.screen()
            return AlertScreen(
                { UScreen.displayScreen(currentScreen) },
                Component.translatable("resourcify.no-vulkan.title"),
                Component.translatable("resourcify.no-vulkan.description")
            )
        }
        //?}
        return callable.invoke()
    }
}