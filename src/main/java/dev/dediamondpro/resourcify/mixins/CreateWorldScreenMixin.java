package dev.dediamondpro.resourcify.mixins;

import dev.dediamondpro.resourcify.config.Config;
import dev.dediamondpro.resourcify.gui.world.DownloadWorldTab;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    //?if <26.2 {
                    /*target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;addTabs([Lnet/minecraft/client/gui/components/tabs/Tab;)Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;"
                    *///?} else
                    target = "Lnet/minecraft/client/gui/components/tabs/MenuTabBar$Builder;addTabs([Lnet/minecraft/client/gui/components/tabs/Tab;)Lnet/minecraft/client/gui/components/tabs/MenuTabBar$Builder;"
            )
    )
    public Tab[] /*? <26.2 { */ /*addTab *//*?} else {*/ addTabs /*?}*/ (Tab[] tabs) {
        if (!Config.Companion.getInstance().getWorldsEnabled()) {
            return tabs;
        }
        Tab[] tabsNew = new Tab[tabs.length + 1];
        System.arraycopy(tabs, 0, tabsNew, 0, tabs.length);
        tabsNew[tabs.length] = new DownloadWorldTab();
        return tabsNew;
    }
}
