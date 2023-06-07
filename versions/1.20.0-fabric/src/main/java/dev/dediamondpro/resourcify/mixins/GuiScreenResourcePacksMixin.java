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

import dev.dediamondpro.resourcify.gui.resourcepack.ResourcePackAddition;
import gg.essential.universal.UMatrixStack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackScreen.class)
class GuiScreenResourcePacksMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void drawScreen(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String title = ((TranslatableTextContent) ((PackScreen) (Object) this).getTitle().getContent()).getKey();
        if (!title.equals("resourcePack.title")) return;
        ResourcePackAddition.INSTANCE.onRender(new UMatrixStack(context.getMatrices()));
    }
}
