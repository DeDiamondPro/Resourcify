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

package dev.dediamondpro.resourcify.util

import net.minecraft.resources.ResourceLocation

object Icons {
    val PLUS = createResourceLocation("plus.png")
    val UPDATE = createResourceLocation("update.png")
    val BACK = createResourceLocation("back.png")
    val FORWARD = createResourceLocation("forward.png")
    val DROPDOWN = createResourceLocation("dropdown.png")
    val EXTERNAL_LINK = createResourceLocation("external-link.png")
    val EXPANDABLE_CLOSED = createResourceLocation("expandable-closed.png")
    val EXPANDABLE_OPENED = createResourceLocation("expandable-opened.png")
    val ADVERTISEMENT_TEXT = createResourceLocation("advertisement-text.png")
    val DEFAULT_ICON = createResourceLocation("default-icon.png")
    val LOADING = createResourceLocation("loading.png")

    private fun createResourceLocation(asset: String): ResourceLocation {
        //? if <1.21.0 {
        /*return ResourceLocation("resourcify", asset)
        *///?} else
        return ResourceLocation.fromNamespaceAndPath("resourcify", asset)
    }
}