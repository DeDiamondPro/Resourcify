package dev.dediamondpro.resourcify.services.curseforge

import com.google.gson.annotations.SerializedName

data class CurseForgeMinecraftVersions(
    @SerializedName("versionString") val name: String,
    @SerializedName("gameVersionTypeId") val id: Int
)

data class CurseForgeMinecraftVersionsResponse(val data: List<CurseForgeMinecraftVersions>)
