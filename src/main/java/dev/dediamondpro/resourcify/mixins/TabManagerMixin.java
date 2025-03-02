package dev.dediamondpro.resourcify.mixins;

import dev.dediamondpro.resourcify.gui.world.DownloadWorldTab;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TabManager.class)
public class TabManagerMixin {

    @Shadow @Nullable private ScreenRectangle tabArea;

    @Inject(method = "setCurrentTab", at = @At("HEAD"), cancellable = true)
    public void onResourcifyWorldTab(Tab tab, boolean playClickSound, CallbackInfo ci) {
        // The tab opens a GUI, so we don't want it to be selected because then the GUI would open again after its closed
        if (tab instanceof DownloadWorldTab) {
            ci.cancel();
            ((DownloadWorldTab) tab).onClick();
        }
    }
}
