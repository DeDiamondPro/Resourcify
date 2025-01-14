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

//#if MC < 11600
//$$
//$$ import dev.dediamondpro.resourcify.elements.MinecraftButton;
//$$ import net.minecraft.client.resources.SimpleReloadableResourceManager;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$ @Mixin(SimpleReloadableResourceManager.class)
//$$ public class ReloadableResourceManagerMixin {
//$$     @Inject(method = "notifyReloadListeners", at = @At("HEAD"))
//$$     void onReload(CallbackInfo ci) {
//$$         MinecraftButton.Companion.reloadTexture();
//$$     }
//$$ }
//$$
//#endif