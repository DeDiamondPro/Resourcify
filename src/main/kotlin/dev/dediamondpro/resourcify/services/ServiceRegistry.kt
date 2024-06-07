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
import dev.dediamondpro.resourcify.services.curseforge.CurseForgeService
import dev.dediamondpro.resourcify.services.modrinth.ModrinthService

object ServiceRegistry {
    private val services = mutableListOf<IService>()

    init {
        registerService(ModrinthService)
        registerService(CurseForgeService)
    }

    fun getServices(): List<IService> {
        return services
    }

    fun getService(name: String): IService? {
        return services.firstOrNull { it.getName() == name }
    }

    fun getDefaultService(): IService {
        return getService(Config.instance.defaultService) ?: services.first()
    }

    fun registerService(service: IService) {
        services.add(service)
    }
}