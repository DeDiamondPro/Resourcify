/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

import dev.dediamondpro.resourcify.gui.pack.PackScreensAddition;
import dev.dediamondpro.resourcify.platform.Platform;
import dev.dediamondpro.resourcify.services.ProjectType;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if MC >= 11800
//$$ import net.minecraft.client.gui.Drawable;
//$$ import net.minecraft.client.gui.Selectable;
//#endif

@Mixin(Screen.class)
abstract
class ScreenMixin {
    //#if MC < 11800
    @Shadow @Final protected List<IGuiEventListener> children;
    @Shadow @Final protected List<Widget> buttons;
    //#else
    //$$ @Shadow @Final private List<Drawable> drawables;
    //$$ @Shadow @Final private List<Selectable> selectables;
    //$$ @Shadow @Final private List<Element> children;
    //#endif

    @Unique private List<Button> resourcifyCustomButtons;

    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At(value = "INVOKE", target =
            //#if MC < 11800
            "Lnet/minecraft/client/gui/screen/Screen;init()V",
            shift = At.Shift.AFTER
            //#else
            //$$ "Lnet/minecraft/client/gui/screen/Screen;narrateScreenIfNarrationEnabled(Z)V",
            //$$ shift = At.Shift.BEFORE
            //#endif
            ))
    private void onInit(CallbackInfo ci) {
        handleInit_Resourcify();
    }

    //#if MC >= 11904
    //$$ @Inject(method = "resize", at = @At("TAIL"))
    //$$ private void onResize(CallbackInfo ci) {
    //$$     handleInit_Resourcify();
    //$$ }
    //#endif

    @Unique
    private void handleInit_Resourcify() {
        String title = Platform.INSTANCE.getTranslateKey((Screen) (Object) this);
        ProjectType type = PackScreensAddition.INSTANCE.getType(title);
        if (type == null) return;
        // Remove old elements if they still exist for some reason
        if (resourcifyCustomButtons != null) {
            //#if MC < 11800
            this.buttons.removeAll(resourcifyCustomButtons);
            this.children.removeAll(resourcifyCustomButtons);
            //#else
            //$$ this.children.removeAll(resourcifyCustomButtons);
            //$$ this.drawables.removeAll(resourcifyCustomButtons);
            //$$ this.selectables.removeAll(resourcifyCustomButtons);
            //#endif
        }
        // Add new elements in the correct location
        resourcifyCustomButtons = PackScreensAddition.INSTANCE.getButtons((Screen) (Object) this, type);
        //#if MC < 11800
        this.buttons.addAll(resourcifyCustomButtons);
        this.children.addAll(resourcifyCustomButtons);
        //#else
        //$$ this.children.addAll(resourcifyCustomButtons);
        //$$ this.drawables.addAll(resourcifyCustomButtons);
        //$$ this.selectables.addAll(resourcifyCustomButtons);
        //#endif
    }
}
