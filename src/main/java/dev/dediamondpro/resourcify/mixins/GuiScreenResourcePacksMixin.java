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

//#if MC < 11600
//$$
//$$ import dev.dediamondpro.resourcify.gui.pack.PackScreensAddition;
//$$ import dev.dediamondpro.resourcify.services.ProjectType;
//$$ import gg.essential.universal.UMatrixStack;
//$$ import gg.essential.universal.UMinecraft;
//$$ import net.minecraft.client.gui.GuiScreenResourcePacks;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ @Mixin(GuiScreenResourcePacks.class)
//$$ public class GuiScreenResourcePacksMixin {
//$$
//$$     @Inject(method = "drawScreen", at = @At("TAIL"))
//$$     private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
//$$         PackScreensAddition.INSTANCE.onRender(UMatrixStack.Compat.INSTANCE.get(), ProjectType.RESOURCE_PACK);
//$$     }
//$$
//$$     @Inject(method = "mouseClicked", at = @At("HEAD"))
//$$     private void mouseClick(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
//$$         PackScreensAddition.INSTANCE.onMouseClick(
//$$                 mouseX, mouseY, mouseButton, ProjectType.RESOURCE_PACK,
//$$                 UMinecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks()
//$$         );
//$$     }
//$$ }
//$$
//#endif