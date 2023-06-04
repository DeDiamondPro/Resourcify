/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.modrinth

import dev.dediamondpro.resourcify.util.getJson
import gg.essential.elementa.components.Window
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URL
import java.util.concurrent.CompletableFuture

@Serializable
data class Categories(
    val icon: String,
    val name: String,
    @SerialName("project_type") val projectType: String,
    val header: String
) {
    companion object {
        private val categories: CompletableFuture<List<Categories>> by lazy {
            CompletableFuture.supplyAsync {
                (URL("https://api.modrinth.com/v2/tag/category").getJson<List<Categories>>() ?: emptyList()).filter {
                    it.projectType == "resourcepack"
                }.sortedBy {
                    it.header +
                            if (it.header != "resolutions") it.name
                            else it.name.replace(Regex("[^0-9]"), "").toInt().toChar()
                }
            }
        }

        fun getCategoriesByHeader(): Map<String, List<Categories>> {
            return categories.get().groupBy { it.header }
        }

        fun getCategoriesByHeaderWhenLoaded(callback: (Map<String, List<Categories>>) -> Unit) {
            if (categories.isDone) callback(categories.get().groupBy { it.header })
            else categories.whenComplete { categories, _ ->
                if (categories == null) return@whenComplete
                Window.enqueueRenderOperation { callback(categories.groupBy { it.header }) }
            }
        }
    }
}