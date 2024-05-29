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