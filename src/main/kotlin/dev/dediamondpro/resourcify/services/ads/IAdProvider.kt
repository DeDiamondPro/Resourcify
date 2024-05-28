package dev.dediamondpro.resourcify.services.ads

interface IAdProvider {
    fun isAdAvailable(): Boolean
    fun getText(): String
    fun getImagePath(): String
    fun getUrl(): String
}