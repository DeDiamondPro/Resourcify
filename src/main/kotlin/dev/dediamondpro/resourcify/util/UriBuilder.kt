/*
 * This file is part of Resourcify
 * Copyright (C) 2025 DeDiamondPro
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

package dev.dediamondpro.resourcify.util

import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Mimics a part of the apache http library since we used this and Mojang doesn't bundle it anymore
 * Very basic implementation but works for what we need
 */
class UriBuilder(private val baseUri: String) {
    private val parameters = mutableListOf<Pair<String, String>>()

    fun addParameter(key: String, value: String): UriBuilder {
        parameters.add(key to value)
        return this
    }

    fun build(): URI {
        if (parameters.isEmpty()) {
            return URI(baseUri)
        }

        fun enc(v: String) = URLEncoder.encode(v, StandardCharsets.UTF_8)
        val queryString = parameters.joinToString("&") { (k, v) ->
            "${enc(k)}=${enc(v)}"
        }

        return URI("$baseUri?$queryString")
    }
}