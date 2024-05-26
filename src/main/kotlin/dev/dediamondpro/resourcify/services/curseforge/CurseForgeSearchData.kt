package dev.dediamondpro.resourcify.services.curseforge

import com.google.gson.annotations.SerializedName
import dev.dediamondpro.resourcify.services.ISearchData

data class CurseForgeSearchData(
    @SerializedName("data") override val projects: List<CurseForgeProject>,
    val pagination: Pagination
) : ISearchData {
    override val totalCount: Int
        get() = pagination.totalCount

    data class Pagination(
        val totalCount: Int
    )
}
