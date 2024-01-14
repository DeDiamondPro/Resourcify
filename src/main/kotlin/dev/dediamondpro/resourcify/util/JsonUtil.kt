package dev.dediamondpro.resourcify.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Reader

val gson: Gson = Gson()

inline fun <reified T> Reader.fromJson(): T {
    val type = object : TypeToken<T>() {}.type
    return gson.fromJson(this, type)
}

inline fun <reified T> String.fromJson(): T {
    val type = object : TypeToken<T>() {}.type
    return gson.fromJson(this, type)
}

inline fun <reified T> T.toJson(): String {
    val type = object : TypeToken<T>() {}.type
    return gson.toJson(this, type)
}