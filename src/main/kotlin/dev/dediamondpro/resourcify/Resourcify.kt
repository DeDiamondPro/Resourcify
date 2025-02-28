/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2025 DeDiamondPro
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

import dev.dediamondpro.resourcify.platform.ThemeReloadListener
import dev.dediamondpro.resourcify.config.SettingsPage

//? if fabric {
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.server.packs.PackType
//?} else if neoforge {
/*import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
//? if >=1.21.4 {
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent
//?} else
/^import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent^/
*///?} else if forge {
/*import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS
*///?}

//? if forgelike
/*@Mod(ModInfo.ID)*/
object Resourcify /*? if fabric {*/ : ClientModInitializer /*?}*/ {
    init {
        //? if neoforge {
        /*LOADING_CONTEXT.registerExtensionPoint(IConfigScreenFactory::class.java) {
            IConfigScreenFactory { _, _ -> SettingsPage() }
        }
        *///?} else if forge {
        /*LOADING_CONTEXT.registerExtensionPoint(ConfigScreenFactory::class.java) {
            ConfigScreenFactory { _, _ -> SettingsPage() }
        }
        *///?}

        //? if forgelike
        /*MOD_BUS.addListener(::registerClientReloadListeners)*/
    }

    //? if forgelike {
    /*fun registerClientReloadListeners(event: /^? if <1.21.4 || forge {^/  /^RegisterClientReloadListenersEvent ^//^?} else {^/ AddClientReloadListenersEvent /^?}^/) {
        //? if <1.21.4 || forge {
        /^event.registerReloadListener(ThemeReloadListener)
        ^///?} else
        event.addListener(ThemeReloadListener.colorsLocation, ThemeReloadListener)
    }
    *///?}

    //? if fabric {
    override fun onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ThemeReloadListener)
    }
    //?}
}
