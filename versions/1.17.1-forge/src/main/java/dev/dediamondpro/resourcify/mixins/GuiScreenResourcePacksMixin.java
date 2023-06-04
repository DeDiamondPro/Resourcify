/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackSelectionScreen.class)
class GuiScreenResourcePacksMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void drawScreen(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        ResourcePackAddition.INSTANCE.onRender(new UMatrixStack(matrixStack));
    }
}
