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

data class Member(
    @SerializedName("team_id") val teamId: String,
    val user: User,
    val role: String,
    val accepted: Boolean,
    val ordering: Int
)

data class User(
    val id: String,
    @SerializedName("github_id") val githubId: Int?,
    val username: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val bio: String?,
    val created: String,
    val role: String,
    val badges: String
)
