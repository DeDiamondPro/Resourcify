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

//? if !fabric {
/*import dev.dediamondpro.resourcify.config.SettingsPage

//? if neoforge {
/^import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
^///?} else if forge {
/^import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
^///?}

@Mod(ModInfo.ID)
object Resourcify {
    init {
        //? if neoforge {
        /^LOADING_CONTEXT.registerExtensionPoint(IConfigScreenFactory::class.java) {
            IConfigScreenFactory { _, _ -> SettingsPage() }
        }
        ^///?} else if forge {
        /^LOADING_CONTEXT.registerExtensionPoint(ConfigScreenFactory::class.java) {
            ConfigScreenFactory { _, _ -> SettingsPage() }
        }
        ^///?}
    }
}
*///?}