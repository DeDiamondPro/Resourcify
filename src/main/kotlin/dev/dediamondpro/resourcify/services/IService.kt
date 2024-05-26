package dev.dediamondpro.resourcify.services

import java.util.concurrent.CompletableFuture

interface IService {
    fun getName(): String

    fun search(
        query: String,
        sortBy: String,
        minecraftVersions: List<String>,
        categories: List<String>,
        offset: Int,
        type: ProjectType
    ): ISearchData?

    fun getMinecraftVersions(): CompletableFuture<Map<String, String>>

    fun canSelectMultipleMinecraftVersions(): Boolean = true

    /**
     * The categories supported by the service for a given project type
     * Key = id
     * Value = display name
     */
    fun getCategories(type: ProjectType): CompletableFuture<Map<String, Map<String, String>>>

    /**
     * The search options supported by the service
     * Key = id
     * Value = display name
     */
    fun getSortOptions(): Map<String, String>
}