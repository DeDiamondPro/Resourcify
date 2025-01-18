/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2025 DeDiamondPro
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

import dev.dediamondpro.resourcify.gui.pack.ImageButton;
import dev.dediamondpro.resourcify.gui.pack.PackScreensAddition;
import dev.dediamondpro.resourcify.platform.Platform;
import dev.dediamondpro.resourcify.services.ProjectType;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
abstract
class ScreenMixin {
    @Shadow @Final private List<Renderable> renderables;
    @Shadow @Final private List<NarratableEntry> narratables;
    @Shadow @Final private List<GuiEventListener> children;

    @Unique private List<ImageButton> resourcifyCustomButtons;

    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/client/gui/screens/Screen;triggerImmediateNarration(Z)V",
            shift = At.Shift.BEFORE
    ))
    private void onInit(CallbackInfo ci) {
        handleInit_Resourcify();
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void onResize(CallbackInfo ci) {
        handleInit_Resourcify();
    }

    @Unique
    private void handleInit_Resourcify() {
        if (resourcifyCustomButtons == null) {
            String title = Platform.INSTANCE.getTranslateKey((Screen) (Object) this);
            ProjectType type = PackScreensAddition.INSTANCE.getType(title);
            if (type == null) {
                return;
            }
            resourcifyCustomButtons = PackScreensAddition.INSTANCE.getButtons((Screen) (Object) this, type);
            if (resourcifyCustomButtons == null) {
                return;
            }
        }

        updateButtons_Resourcify(this.children);
        updateButtons_Resourcify(this.renderables);
        updateButtons_Resourcify(this.narratables);
    }

    @Unique
    private <T> void updateButtons_Resourcify(List<T> list) {
        for (ImageButton button : resourcifyCustomButtons) {
            button.updateLocation((Screen) (Object) this);
            if (list.contains(button)) {
                continue;
            }
            list.add((T) button);
        }
    }
}
