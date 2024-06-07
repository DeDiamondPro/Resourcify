/*
 * This file is part of Resourcify
 * Copyright (C) 2024 DeDiamondPro
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

package dev.dediamondpro.resourcify.config

//#if FORGE == 1 && MC <= 11202

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory

class ForgeGuiFactory : IModGuiFactory  {
    override fun initialize(minecraft: Minecraft?) {
    }

    //#if MC==10809
    override fun mainConfigGuiClass(): Class<out GuiScreen> {
        return SettingsPage::class.java
    }

    override fun getHandlerFor(runtimeOptionCategoryElement: IModGuiFactory.RuntimeOptionCategoryElement?): IModGuiFactory.RuntimeOptionGuiHandler? {
        return null
    }
    //#else
    //$$ override fun createConfigGui(guiScreen: GuiScreen?): GuiScreen {
    //$$    return SettingsPage()
    //$$ }
    //$$
    //$$ override fun hasConfigGui(): Boolean = true
    //#endif

    override fun runtimeGuiCategories(): MutableSet<IModGuiFactory.RuntimeOptionCategoryElement>? {
        return null
    }
}

//#endif