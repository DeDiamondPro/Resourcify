package dev.dediamondpro.resourcify.services.modrinth

import com.google.gson.annotations.SerializedName

data class ModrinthMinecraftVersion(val version: String, @SerializedName("version_type") val versionType: String,)
