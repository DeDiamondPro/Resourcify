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

package dev.dediamondpro.resourcify.config

import dev.dediamondpro.resourcify.services.modrinth.ModrinthService
import dev.dediamondpro.resourcify.util.fromJson
import dev.dediamondpro.resourcify.util.toJson
import java.io.File

class Config {
    var defaultService: String = ModrinthService.getName()
    var fullResThumbnail: Boolean = false
    var adsEnabled: Boolean = true
    var resourcePacksEnabled: Boolean = true
    var dataPacksEnabled: Boolean = true
    var shaderPacksEnabled: Boolean = true
    var worldsEnabled: Boolean = true

    companion object {
        private val configFile = File("./config/resourcify.json")
        val instance: Config = load()

        private fun load(): Config {
            return try {
                configFile.readText().fromJson()
            } catch (e: Exception) {
                val config = Config()
                save(config)
                config
            }
        }

        fun save(config: Config = instance) {
            configFile.outputStream().bufferedWriter().use {
                it.write(config.toJson())
            }
        }
    }
}