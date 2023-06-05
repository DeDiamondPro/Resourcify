/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.mixins;

//#if FABRIC==1

import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParentElement.class)
public interface ParentElementMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"))
    default void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof PackScreen)) return;
        String title = ((TranslatableTextContent) ((PackScreen) this).getTitle().getContent()).getKey();
        if (!title.equals("resourcePack.title")) return;
        ResourcePackAddition.INSTANCE.onMouseClick(mouseX, mouseY, button);
    }
}

//#endif