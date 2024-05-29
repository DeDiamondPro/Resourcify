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