/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
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

//#if FABRIC == 1

import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition;
import dev.dediamondpro.resourcify.modrinth.ApiInfo;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Pseudo
@Mixin(targets = "net.coderbot.iris.gui.screen.ShaderPackScreen", remap = false)
public class IrisShaderScreenMixin {
    @Inject(method = "method_25394", at = @At("RETURN"))
    void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ResourcePackAddition.INSTANCE.onRender(new UMatrixStack(context.getMatrices()), ApiInfo.ProjectType.IRIS_SHADER);
    }

    @Inject(method = "method_25402", at = @At("HEAD"))
    void onMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        ResourcePackAddition.INSTANCE.onMouseClick(mouseX, mouseY, button, ApiInfo.ProjectType.IRIS_SHADER, new File("./shaderpacks"));
    }
}

//#endif