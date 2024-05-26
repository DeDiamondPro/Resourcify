package dev.dediamondpro.resourcify.services.modrinth

import com.google.gson.annotations.SerializedName
import dev.dediamondpro.resourcify.services.ISearchData

data class ModrinthSearchData(
    @SerializedName("hits") override val projects: List<ModrinthProject>,
    @SerializedName("total_hits") override val totalCount: Int
) : ISearchData