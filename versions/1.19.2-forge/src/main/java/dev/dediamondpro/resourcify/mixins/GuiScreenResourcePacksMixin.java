/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License Version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dediamondpro.resourcify.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackSelectionScreen.class)
class GuiScreenResourcePacksMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void drawScreen(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, CallbackInfo ci){
        String title = ((TranslatableContents) ((PackSelectionScreen) (Object) this).getTitle().getContents()).getKey();
        if (!title.equals("resourcePack.title")) return;
        ResourcePackAddition.INSTANCE.onRender(new UMatrixStack(matrixStack));
    }
}
