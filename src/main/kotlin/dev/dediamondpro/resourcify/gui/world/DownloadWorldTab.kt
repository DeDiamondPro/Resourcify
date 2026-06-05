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

package dev.dediamondpro.resourcify.gui.world

import dev.dediamondpro.resourcify.gui.browsepage.BrowseScreen
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.services.ProjectType
import gg.essential.universal.UScreen
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.tabs.Tab
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.network.chat.Component
import java.util.function.Consumer

//?if >=26.2 {
/*import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.layouts.LinearLayout

*///?}

class DownloadWorldTab : Tab {
    override fun getTabTitle(): Component {
        return Component.translatable("resourcify.world.download")
    }

    //? if >= 1.21.6 {
    override fun getTabExtraNarration(): Component {
        return Component.empty()
    }
    //?}

    override fun visitChildren(consumer: Consumer<AbstractWidget>) {
    }

    override fun doLayout(rectangle: ScreenRectangle) {
    }

    //?if >=26.2 {
    /*val layout: LinearLayout = LinearLayout.vertical()

    override fun getLayout(): Layout {
        return layout
    }
    *///?}

    fun onClick() {
        UScreen.displayScreen(BrowseScreen(ProjectType.WORLD, Platform.getFileInGameDir("saves")))
    }
}