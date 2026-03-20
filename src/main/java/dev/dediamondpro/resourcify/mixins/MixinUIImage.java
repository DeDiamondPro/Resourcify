/*
 * This file is part of Resourcify
 * Copyright (C) 2026 DeDiamondPro
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dediamondpro.resourcify.mixins;

//? if >=1.21.11 {

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import dev.dediamondpro.resourcify.util.UFilterHandler;
import gg.essential.elementa.utils.ImageKt;
import gg.essential.universal.UMatrixStack;
import gg.essential.universal.utils.ReleasedDynamicTexture;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ImageKt.class)
public class MixinUIImage {

    @Inject(method = "drawTexture", at = @At("HEAD"), remap = false)
    private static void drawTexturePre(UMatrixStack matrixStack, ReleasedDynamicTexture texture, Color color, double x, double y, double width, double height, int textureMinFilter, int textureMagFilter, CallbackInfo ci) {
        FilterMode minFilter = null;
        FilterMode magFilter = null;
        switch (textureMinFilter) {
            case GL11.GL_NEAREST -> minFilter = FilterMode.NEAREST;
            case GL11.GL_LINEAR -> minFilter = FilterMode.LINEAR;
        }
        switch (textureMagFilter) {
            case GL11.GL_NEAREST -> magFilter = FilterMode.NEAREST;
            case GL11.GL_LINEAR -> magFilter = FilterMode.LINEAR;
        }
        if (minFilter != null && magFilter != null) {
            UFilterHandler.activeSampler = RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, minFilter, magFilter, false);
        }
    }

    @Inject(method = "drawTexture", at = @At("TAIL"), remap = false)
    private static void drawTexturePost(UMatrixStack matrixStack, ReleasedDynamicTexture texture, Color color, double x, double y, double width, double height, int textureMinFilter, int textureMagFilter, CallbackInfo ci) {
        UFilterHandler.activeSampler = null;
    }
}
//?}