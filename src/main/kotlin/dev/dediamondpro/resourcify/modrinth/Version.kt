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

import dev.dediamondpro.resourcify.util.capitalizeAll
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
data class Version(
    val id: String,
    @SerialName("project_id") val projectId: String,
    @SerialName("author_id") val authorId: String,
    val featured: Boolean,
    val name: String,
    @SerialName("version_number") val versionNumber: String,
    val changelog: String,
    @SerialName("date_published") val datePublished: String,
    val downloads: Int,
    @SerialName("version_type") val versionType: VersionType,
    val status: String,
    val files: List<VersionFile>,
    @SerialName("game_versions") val gameVersions: List<String>,
    val loaders: List<String>
) {
    val primaryFile by lazy { files.firstOrNull { it.primary } ?: files.firstOrNull() }
}

@Serializable
data class VersionFile(
    val hashes: VersionFileHashes,
    val url: String,
    @SerialName("filename") val fileName: String,
    val primary: Boolean,
    val size: Int,
)

@Serializable
data class VersionFileHashes(val sha512: String, val sha1: String)

@Serializable
enum class VersionType {
    @SerialName("release")
    RELEASE,

    @SerialName("snapshot")
    SNAPSHOT,

    @SerialName("beta")
    BETA,

    @SerialName("alpha")
    ALPHA;

    val formattedName = name.lowercase().capitalizeAll()

    val color = when (name) {
        "RELEASE" -> Color(27, 217, 106)
        "BETA" -> Color(255, 163, 71)
        else -> Color(255, 73, 110)
    }
}

data class Dependency(
    @SerialName("version_id") val versionId: String,
    @SerialName("project_id") val projectId: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("dependency_type") val dependencyType: DependencyType
)

@Serializable
enum class DependencyType {
    @SerialName("required")
    REQUIRED,

    @SerialName("optional")
    OPTIONAL,

    @SerialName("incompatible")
    INCOMPATIBLE,

    @SerialName("embedded")
    EMBEDDED
}
