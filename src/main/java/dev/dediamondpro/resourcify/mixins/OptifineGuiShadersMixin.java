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

//#if FORGE == 1

import dev.dediamondpro.resourcify.gui.resourcepack.PackScreensAddition;
import dev.dediamondpro.resourcify.modrinth.ApiInfo;
import gg.essential.universal.UMatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11700
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#elseif MC >= 11600
//$$ import com.mojang.blaze3d.matrix.MatrixStack;
//#endif

@Pseudo
@Mixin(targets = "net.optifine.shaders.gui.GuiShaders", remap = false)
public class OptifineGuiShadersMixin {

    @Inject(
            method =
                    //#if MC >= 11904
                    //$$ "m_86412_",
                    //#elseif MC >= 11700
                    //$$ "m_6305_",
                    //#elseif MC >= 11600
                    //$$ "func_230430_a_",
                    //#else
                    "func_73863_a",
                    //#endif
            at = @At("RETURN"), remap = false)
    void onDraw(
            //#if MC >= 11700
            //$$ PoseStack matrixStack,
            //#elseif MC >= 11600
            //$$ MatrixStack matrixStack,
            //#endif
            int mouseX, int mouseY, float partialTicks, CallbackInfo ci
    ) {
        PackScreensAddition.INSTANCE.onRender(
                //#if MC < 11600
                UMatrixStack.Compat.INSTANCE.get(),
                //#else
                //$$ new UMatrixStack(matrixStack),
                //#endif
                ApiInfo.ProjectType.OPTIFINE_SHADER
        );
    }
}

//#endif