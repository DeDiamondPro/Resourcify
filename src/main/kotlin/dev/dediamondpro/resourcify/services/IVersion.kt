package dev.dediamondpro.resourcify.services

import java.util.concurrent.CompletableFuture

interface IVersion {
    fun getName(): String
    fun getVersionNumber(): String?
    fun getDownloadUrl(): String
    fun getFileName(): String
    fun getSha1(): String
    fun getChangeLog(): CompletableFuture<String>
    fun getVersionType(): VersionType
    fun getLoaders(): List<String>
    fun getMinecraftVersions(): List<String>
    fun getDownloadCount(): Int
    fun getReleaseDate(): String
}