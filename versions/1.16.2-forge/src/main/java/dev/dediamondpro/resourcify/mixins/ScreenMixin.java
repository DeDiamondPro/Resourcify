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

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.dediamondpro.resourcify.gui.pack.PackScreensAddition;
import dev.dediamondpro.resourcify.modrinth.ApiInfo;
import dev.dediamondpro.resourcify.platform.Platform;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
class ScreenMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void drawScreen(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        String title = Platform.INSTANCE.getTranslateKey((Screen) (Object) this);
        ApiInfo.ProjectType type = PackScreensAddition.INSTANCE.getType(title);
        if (type == null) return;
        PackScreensAddition.INSTANCE.onRender(new UMatrixStack(matrixStack), type);
    }
}
