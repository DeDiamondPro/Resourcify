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

package dev.dediamondpro.resourcify.platform

//#if FORGELIKE==1 && MODERN == 1
//$$ import dev.dediamondpro.resourcify.gui.pack.PackScreensAddition
//$$ import dev.dediamondpro.resourcify.platform.Platform
//$$
//$$ //#if FORGE == 1
//$$ import net.minecraftforge.eventbus.api.SubscribeEvent
//$$ //#if MC <= 11800
//$$ import net.minecraftforge.client.event.GuiScreenEvent
//$$ //#else
//$$ //$$ import net.minecraftforge.client.event.ScreenEvent
//$$ //#endif
//$$ //#elseif NEOFORGE == 1
//$$ //$$ import net.neoforged.bus.api.SubscribeEvent
//$$ //$$ import net.neoforged.neoforge.client.event.ScreenEvent
//$$ //#endif
//$$
//$$
//$$ object EventHandler {
//$$
//$$     @SubscribeEvent
//$$     fun onMouseClicked(
//$$        event:
//$$        //#if MC <= 11800
//$$        GuiScreenEvent
//$$        //#else
//$$        //$$ ScreenEvent
//$$        //#endif
//$$            //#if MC < 11900
//$$            .MouseClickedEvent
//$$            //#else
//$$            //$$ .MouseButtonPressed
//$$            //#endif
//$$            .Pre
//$$     ) {
//$$         //#if MC <= 11800
//$$         val screen = event.gui
//$$         //#else
//$$         //$$ val screen = event.screen
//$$         //#endif
//$$         val title = Platform.getTranslateKey(screen)
//$$         val type = PackScreensAddition.getType(title) ?: return
//$$         PackScreensAddition.onMouseClick(
//$$             event.mouseX, event.mouseY,
//$$             event.button, type,
//$$             type.getDirectory(screen)
//$$         )
//$$     }
//$$ }
//#endif