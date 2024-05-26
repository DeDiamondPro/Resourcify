package dev.dediamondpro.resourcify.services

import java.awt.Color
import java.util.concurrent.CompletableFuture

interface IProject {
    fun getName(): String
    fun getSummary(): String
    fun getAuthor(): String
    fun getIconUrl(): String? = null
    fun getBannerUrl(): String? = null
    fun getBannerColor(): Color? = null
    fun getBrowserUrl(): String
    fun getDescription(): CompletableFuture<String>
    fun getCategories(): CompletableFuture<List<String>>
    fun getExternalLinks(): CompletableFuture<Map<String, String>>
    fun getMembers(): CompletableFuture<List<IMember>>
    fun hasGallery(): Boolean
    fun getGalleryImages(): CompletableFuture<List<IGalleryImage>>
    fun canBeInstalled(): Boolean = true
    fun getVersions(): CompletableFuture<List<IVersion>>
}