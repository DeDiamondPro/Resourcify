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

import dev.dediamondpro.resourcify.gui.injections.ImageButton;
import dev.dediamondpro.resourcify.gui.injections.PackScreensAddition;
import dev.dediamondpro.resourcify.services.ProjectType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(targets = "dev.vitrail.screen.SettingsScreen", remap = false)
abstract class VitrailScreenMixin extends Screen {
    @Shadow
    private boolean guiHidden;

    protected VitrailScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void resourcifyAddButtons(CallbackInfo ci) {
        if (guiHidden) {
            return;
        }
        // vitrail clears and rebuilds its widgets when switching views or hiding the gui.
        List<ImageButton> buttons = PackScreensAddition.INSTANCE.getButtons(this, ProjectType.VITRAIL_SHADER);
        if (buttons != null) {
            for (ImageButton button : buttons) {
                addRenderableWidget(button);
            }
        }
    }
}
