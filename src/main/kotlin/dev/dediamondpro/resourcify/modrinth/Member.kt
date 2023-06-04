/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    @SerialName("team_id") val teamId: String,
    val user: User,
    val role: String,
    val accepted: Boolean,
    val ordering: Int
)

@Serializable
data class User(
    val id: String,
    @SerialName("github_id") val githubId: Int?,
    val username: String,
    @SerialName("avatar_url") val avatarUrl: String,
    val bio: String?,
    val created: String,
    val role: String,
    val badges: String
)
