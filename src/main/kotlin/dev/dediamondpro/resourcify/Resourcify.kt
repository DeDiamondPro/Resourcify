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

//#if FORGE == 1

package dev.dediamondpro.resourcify

//#if MODERN == 0
import net.minecraftforge.fml.common.Mod
//#else
//$$ import dev.dediamondpro.resourcify.platform.EventHandler
//$$ import thedarkcolour.kotlinforforge.forge.FORGE_BUS
//$$ import thedarkcolour.kotlinforforge.forge.MOD_BUS
//$$ import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
//$$ import net.minecraftforge.fml.common.Mod
//#endif

//#if MODERN == 0
@Mod(
    name = ModInfo.NAME,
    modid = ModInfo.ID,
    version = ModInfo.VERSION,
    modLanguageAdapter = "dev.dediamondpro.resourcify.platform.KotlinLanguageAdapter"
)
//#else
//$$ @Mod(ModInfo.ID)
//#endif
object Resourcify {
    //#if MC > 11202
    //$$ init {
    //$$     MOD_BUS.register(this::onClientInit)
    //$$ }
    //$$
    //$$ private fun onClientInit(event: FMLClientSetupEvent) {
    //$$     FORGE_BUS.register(EventHandler)
    //$$ }
    //#endif
}

//#endif