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