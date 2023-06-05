/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackScreen.class)
class GuiScreenResourcePacksMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void drawScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        String title = ((TranslationTextComponent) ((PackScreen) (Object) this).getTitle()).getKey();
        if (!title.equals("resourcePack.title")) return;
        ResourcePackAddition.INSTANCE.onRender(new UMatrixStack(matrixStack));
    }
}
