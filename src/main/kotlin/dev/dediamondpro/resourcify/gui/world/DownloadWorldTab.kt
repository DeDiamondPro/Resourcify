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

    fun onClick() {
        UScreen.displayScreen(BrowseScreen(ProjectType.WORLD, Platform.getFileInGameDir("saves")))
    }
}