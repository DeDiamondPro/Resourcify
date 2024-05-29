package dev.dediamondpro.resourcify.config

//#if FABRIC==1

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screen.Screen

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return SettingsFactory
    }

    object SettingsFactory : ConfigScreenFactory<SettingsPage> {
        override fun create(parent: Screen?): SettingsPage = SettingsPage()
    }
}

//#endif