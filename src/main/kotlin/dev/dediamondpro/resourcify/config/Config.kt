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

// decodeFromString import is required on older kotlin versions
@file:Suppress("unusedImport")

package dev.dediamondpro.resourcify.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Config(val checkForUpdates: Boolean = true, var ignoredVersions: MutableList<String> = mutableListOf()) {

    companion object {
        private val configFile = File("config/resourcify.json")
        private val json = Json {
            encodeDefaults = true
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }

        val INSTANCE by lazy {
            try {
                json.decodeFromString(configFile.readText())
            } catch (_: Exception) {
                val config =  Config()
                configFile.writeText(json.encodeToString(config))
                config
            }
        }

        fun save() {
            configFile.writeText(json.encodeToString(INSTANCE))
        }
    }
}
