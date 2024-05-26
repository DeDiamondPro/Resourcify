package dev.dediamondpro.resourcify.services.modrinth

import com.google.gson.annotations.SerializedName

data class ModrinthCategory(
    val name: String,
    @SerializedName("project_type") val projectType: String,
    val header: String
)
