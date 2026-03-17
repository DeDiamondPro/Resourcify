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

//?if >=1.21.11{

import com.mojang.blaze3d.textures.GpuSampler;
import dev.dediamondpro.resourcify.util.UFilterHandler;
import gg.essential.universal.render.URenderPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(URenderPass.DrawCallBuilderImpl.class)
public class MixinURenderPass {

    // This mixin will only work outside of dev env, when UniversalCraft is relocated
    @ModifyArg(
            method = "texture(Ljava/lang/String;I)Ldev/dediamondpro/resourcify/libs/universal/render/DrawCallBuilder;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderPass;bindTexture(Ljava/lang/String;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;)V"
            ),
            index = 2,
            remap = false
    )
    GpuSampler setFilterMode(GpuSampler original) {
        return Objects.requireNonNullElse(UFilterHandler.activeSampler, original);
    }
}

//?}
