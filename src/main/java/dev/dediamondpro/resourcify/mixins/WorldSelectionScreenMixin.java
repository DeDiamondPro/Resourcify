/*
 * This file is part of Resourcify
 * Copyright (C) 2024 DeDiamondPro
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

//#if MC < 11600

import dev.dediamondpro.resourcify.gui.pack.PackScreensAddition;
import dev.dediamondpro.resourcify.services.ProjectType;
import gg.essential.universal.UMatrixStack;
import gg.essential.universal.UMouse;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GuiSelectWorld.class)
abstract public class WorldSelectionScreenMixin extends GuiScreen {

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        PackScreensAddition.INSTANCE.onRender(UMatrixStack.Compat.INSTANCE.get(), ProjectType.WORLD);
    }

    @Inject(method = "handleMouseInput", at = @At("TAIL"))
    private void mouseClick(CallbackInfo ci) {
        if (!Mouse.getEventButtonState()) {
            return;
        }

        PackScreensAddition.INSTANCE.onMouseClick(
                UMouse.Scaled.getX(), UMouse.Scaled.getY(), Mouse.getEventButton(), ProjectType.WORLD,
                new File("./saves")
        );
    }
}

//#endif