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

package dev.dediamondpro.resourcify.services

import java.awt.Color
import java.net.URL
import java.util.concurrent.CompletableFuture

interface IProject {
    fun getName(): String
    fun getId(): String
    fun getSummary(): String
    fun getAuthor(): String
    fun getIconUrl(): URL? = null
    fun getBannerUrl(): URL? = null
    fun getBannerColor(): Color? = null
    fun getBrowserUrl(): String
    fun getDescription(): CompletableFuture<String>
    fun getCategories(): CompletableFuture<List<String>>
    fun getExternalLinks(): CompletableFuture<Map<String, String>>
    fun getMembers(): CompletableFuture<List<IMember>>
    fun hasGallery(): Boolean
    fun getGalleryImages(): CompletableFuture<List<IGalleryImage>>
    fun canBeInstalled(): Boolean = true
    fun getVersions(): CompletableFuture<List<IVersion>>
}