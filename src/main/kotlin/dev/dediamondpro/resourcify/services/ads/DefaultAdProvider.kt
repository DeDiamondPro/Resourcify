/*
 * This file is part of Resourcify
 * Copyright (C) 2024-2025 DeDiamondPro
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

package dev.dediamondpro.resourcify.services.ads

import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.util.localize

object DefaultAdProvider : IAdProvider {
    override fun isAdAvailable(): Boolean = Config.instance.adsEnabled
    override fun getText(): String = "resourcify.browse.bisect_ad".localize()
    override fun getImagePath(): String = "/assets/resourcify/textures/bisect-logo.png"
    override fun getUrl(): String = "https://bisecthosting.com/diamond?r=resourcify"
}