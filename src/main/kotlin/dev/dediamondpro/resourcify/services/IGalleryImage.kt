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

import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.util.toURIOrNull
import java.net.URI

interface IGalleryImage {
    val url: String
    val thumbnailUrl: String?
    val title: String?
    val description: String?

    fun getThumbnailUrlIfEnabled(): URI? {
        if (Config.instance.fullResThumbnail) return url.toURIOrNull()
        return thumbnailUrl?.toURIOrNull() ?: url.toURIOrNull()
    }
}