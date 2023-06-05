/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.mixins;

import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackScreen.class)
class GuiScreenResourcePacksMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void drawScreen(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String title = ((TranslatableTextContent) ((PackScreen) (Object) this).getTitle().getContent()).getKey();
        if (!title.equals("resourcePack.title")) return;
        ResourcePackAddition.INSTANCE.onRender(new UMatrixStack(matrices));
    }
}
