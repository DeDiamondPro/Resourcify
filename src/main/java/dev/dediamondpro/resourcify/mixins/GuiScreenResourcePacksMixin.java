/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.mixins;

import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenResourcePacks.class)
public class GuiScreenResourcePacksMixin {

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ResourcePackAddition.INSTANCE.onRender(UMatrixStack.Compat.INSTANCE.get());
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void mouseClick(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        ResourcePackAddition.INSTANCE.onMouseClick(mouseX, mouseY, mouseButton);
    }
}