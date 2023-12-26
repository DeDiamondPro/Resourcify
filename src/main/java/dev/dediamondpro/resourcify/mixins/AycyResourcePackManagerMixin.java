package dev.dediamondpro.resourcify.mixins;

//#if MC == 10809

import dev.dediamondpro.resourcify.gui.pack.PackScreensAddition;
import dev.dediamondpro.resourcify.modrinth.ApiInfo;
import gg.essential.universal.UMatrixStack;
import gg.essential.universal.UMinecraft;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// This solution is less than ideal but a @Pseudo mixin threw a ClassNotFoundException
@Mixin(GuiScreen.class)
public class AycyResourcePackManagerMixin {
    @Inject(method = "drawScreen", at = @At("TAIL"))
    void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!this.getClass().getName().equals("me.aycy.resourcepackmanager.gui.screens.GuiResourcePacks")) return;
        PackScreensAddition.INSTANCE.onRender(UMatrixStack.Compat.INSTANCE.get(), ApiInfo.ProjectType.AYCY_RESOURCE_PACK);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (!this.getClass().getName().equals("me.aycy.resourcepackmanager.gui.screens.GuiResourcePacks")) return;
        PackScreensAddition.INSTANCE.onMouseClick(
                mouseX, mouseY, mouseButton, ApiInfo.ProjectType.AYCY_RESOURCE_PACK,
                UMinecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks()
        );
    }
}

//#endif