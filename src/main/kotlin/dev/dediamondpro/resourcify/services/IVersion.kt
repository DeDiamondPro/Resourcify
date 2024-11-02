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

import java.net.URL
import java.util.concurrent.CompletableFuture

interface IVersion {
    fun getName(): String
    fun getVersionNumber(): String?
    fun getProjectId(): String
    fun getDownloadUrl(): URL?
    fun getFileName(): String
    fun getSha1(): String
    fun getChangeLog(): CompletableFuture<String>
    fun getVersionType(): VersionType
    fun getLoaders(): List<String>
    fun getMinecraftVersions(): List<String>
    fun getDownloadCount(): Int
    fun getReleaseDate(): String
    fun hasDependencies(): Boolean
    fun getDependencies(): CompletableFuture<List<IDependency>>
}