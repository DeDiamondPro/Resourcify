/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

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
            prettyPrint = false
            ignoreUnknownKeys = true
            isLenient = true
        }

        val INSTANCE by lazy {
            try {
                json.decodeFromString(configFile.readText())
            } catch (_: Exception) {
                Config()
            }
        }

        fun save() {
            configFile.writeText(json.encodeToString(INSTANCE))
        }
    }
}
