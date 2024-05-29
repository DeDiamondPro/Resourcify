package dev.dediamondpro.resourcify.config

import dev.dediamondpro.resourcify.services.modrinth.ModrinthService
import dev.dediamondpro.resourcify.util.fromJson
import dev.dediamondpro.resourcify.util.toJson
import java.io.File

class Config {
    var defaultService: String = ModrinthService.getName()
    var adsEnabled: Boolean = true

    companion object {
        private val configFile = File("./config/resourcify.json")
        val instance: Config = load()

        private fun load(): Config {
            return try {
                configFile.readText().fromJson()
            } catch (e: Exception) {
                Config()
            }
        }

        fun save() {
            configFile.outputStream().bufferedWriter().use {
                it.write(instance.toJson())
            }
        }
    }
}