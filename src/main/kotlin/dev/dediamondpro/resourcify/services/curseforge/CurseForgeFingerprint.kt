/*
 * This file is part of Resourcify
 * Copyright (C) 2024 DeDiamondPro
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

package dev.dediamondpro.resourcify.services.curseforge

data class CurseForgeFingerprint(val fingerprints: List<Long>)

data class CurseForgeFingerprintResponse(val data: CurseForgeFingerprintMatchResult)

data class CurseForgeFingerprintMatchResult(val exactMatches: List<CurseForgeFingerprintMatch>)

data class CurseForgeFingerprintMatch(
    val id: Int,
    val file: CurseForgeVersion,
    val latestFiles: List<CurseForgeVersion>
)