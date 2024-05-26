package dev.dediamondpro.resourcify.services

interface ISearchData {
    val projects: List<IProject>
    val totalCount: Int
}