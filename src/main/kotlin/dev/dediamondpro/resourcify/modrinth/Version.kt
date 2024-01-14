/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dediamondpro.resourcify.modrinth

import com.google.gson.annotations.SerializedName
import dev.dediamondpro.resourcify.util.capitalizeAll
import java.awt.Color

data class Version(
    val id: String,
    @SerializedName("project_id") val projectId: String,
    @SerializedName("author_id") val authorId: String,
    val featured: Boolean,
    val name: String,
    @SerializedName("version_number") val versionNumber: String,
    val changelog: String,
    @SerializedName("date_published") val datePublished: String,
    val downloads: Int,
    @SerializedName("version_type") val versionType: VersionType,
    val status: String,
    val files: List<VersionFile>,
    @SerializedName("game_versions") val gameVersions: List<String>,
    val loaders: List<String>
) {
    fun getPrimaryFile() = files.firstOrNull { it.primary } ?: files.firstOrNull()
}

data class VersionFile(
    val hashes: VersionFileHashes,
    val url: String,
    @SerializedName("filename") val fileName: String,
    val primary: Boolean,
    val size: Int,
)

data class VersionFileHashes(val sha512: String, val sha1: String)

enum class VersionType {
    @SerializedName("release")
    RELEASE,

    @SerializedName("snapshot")
    SNAPSHOT,

    @SerializedName("beta")
    BETA,

    @SerializedName("alpha")
    ALPHA;

    val formattedName = name.lowercase().capitalizeAll()

    val color = when (name) {
        "RELEASE" -> Color(27, 217, 106)
        "BETA" -> Color(255, 163, 71)
        else -> Color(255, 73, 110)
    }
}

data class Dependency(
    @SerializedName("version_id") val versionId: String,
    @SerializedName("project_id") val projectId: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("dependency_type") val dependencyType: DependencyType
)

enum class DependencyType {
    @SerializedName("required")
    REQUIRED,

    @SerializedName("optional")
    OPTIONAL,

    @SerializedName("incompatible")
    INCOMPATIBLE,

    @SerializedName("embedded")
    EMBEDDED
}
