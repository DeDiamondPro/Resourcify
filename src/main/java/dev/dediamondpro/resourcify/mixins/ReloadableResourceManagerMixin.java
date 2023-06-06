/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.mixins;

import dev.dediamondpro.resourcify.elements.MinecraftButton;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleReloadableResourceManager.class)
public class ReloadableResourceManagerMixin {
    @Inject(method = "notifyReloadListeners", at = @At("HEAD"))
    void onReload(CallbackInfo ci) {
        MinecraftButton.Companion.reloadTexture((IResourceManager) this);
    }
}