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

import dev.dediamondpro.resourcify.elements.MinecraftButton;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SimpleReloadableResourceManager.class)
public class ReloadableResourceManagerMixin {
    //#if MC >= 11800
    //$$ @Inject(method = "reload", at = @At("RETURN"))
    //#else
    @Inject(method = "reloadResources", at = @At("RETURN"))
    //#endif
    void onReload(Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> waitingFor, List<IResourcePack> resourcePacks, CallbackInfoReturnable<IAsyncReloader> cir) {
        MinecraftButton.Companion.reloadTexture();
    }
}