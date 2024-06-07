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

package dev.dediamondpro.resourcify.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.Reader

object JsonUtil {
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()
}

inline fun <reified T> Reader.fromJson(): T {
    val type = object : TypeToken<T>() {}.type
    return JsonUtil.gson.fromJson(this, type)
}

inline fun <reified T> String.fromJson(): T {
    val type = object : TypeToken<T>() {}.type
    return JsonUtil.gson.fromJson(this, type)
}

inline fun <reified T> T.toJson(): String {
    val type = object : TypeToken<T>() {}.type
    return JsonUtil.gson.toJson(this, type)
}