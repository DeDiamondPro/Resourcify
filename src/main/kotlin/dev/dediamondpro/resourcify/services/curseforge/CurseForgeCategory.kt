package dev.dediamondpro.resourcify.services.curseforge

data class CurseForgeCategory(val name: String, val id: Int, val classId: Int?, val isClass: Boolean = false)

data class CurseForgeCategoryResponse(val data: List<CurseForgeCategory>)
