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

//#if MC == 10809
//$$
//$$ import dev.dediamondpro.resourcify.gui.pack.PackScreensAddition;
//$$ import dev.dediamondpro.resourcify.services.ProjectType;
//$$ import gg.essential.universal.UMatrixStack;
//$$ import gg.essential.universal.UMinecraft;
//$$ import net.minecraft.client.gui.GuiScreen;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ // This solution is less than ideal but a @Pseudo mixin threw a ClassNotFoundException
//$$ @Mixin(GuiScreen.class)
//$$ public class AycyResourcePackManagerMixin {
//$$     @Inject(method = "drawScreen", at = @At("TAIL"))
//$$     void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
//$$         if (!this.getClass().getName().equals("me.aycy.resourcepackmanager.gui.screens.GuiResourcePacks")) return;
//$$         PackScreensAddition.INSTANCE.onRender(UMatrixStack.Compat.INSTANCE.get(), ProjectType.AYCY_RESOURCE_PACK);
//$$     }
//$$
//$$     @Inject(method = "mouseClicked", at = @At("HEAD"))
//$$     private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
//$$         if (!this.getClass().getName().equals("me.aycy.resourcepackmanager.gui.screens.GuiResourcePacks")) return;
//$$         PackScreensAddition.INSTANCE.onMouseClick(
//$$                 mouseX, mouseY, mouseButton, ProjectType.AYCY_RESOURCE_PACK,
//$$                 UMinecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks()
//$$         );
//$$     }
//$$ }
//$$
//#endif