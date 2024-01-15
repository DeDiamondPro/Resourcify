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
import dev.dediamondpro.resourcify.util.getJsonAsync
import dev.dediamondpro.resourcify.util.localizeOrDefault
import gg.essential.elementa.components.Window
import java.net.URL
import java.util.concurrent.CompletableFuture

data class Categories(
    val icon: String,
    val name: String,
    @SerializedName("project_type") val projectType: String,
    val header: String
) {
    val localizedName
        get() = "resourcify.categories.$projectType.${name.lowercase().replace(" ", "_")}".localizeOrDefault(
            name.capitalizeAll()
        )

    companion object {
        private val categories: CompletableFuture<List<Categories>> by lazy {
            URL("https://api.modrinth.com/v2/tag/category").getJsonAsync<List<Categories>>(useCache = false).thenApply {
                (it ?: emptyList()).sortedBy { category ->
                    category.header + if (category.header != "resolutions") category.name
                    else category.name.replace(Regex("[^0-9]"), "").toInt().toChar()
                }
            }
        }

        fun getCategoriesByHeaderWhenLoaded(
            filter: (Categories) -> Boolean,
            callback: (Map<String, List<Categories>>) -> Unit
        ) {
            if (categories.isDone) callback((categories.get() ?: emptyList()).filter { filter(it) }
                .groupBy { it.localizeHeader() })
            else categories.whenComplete { categories, _ ->
                Window.enqueueRenderOperation {
                    callback((categories ?: emptyList()).filter { filter(it) }.groupBy { it.localizeHeader() })
                }
            }
        }

        private fun Categories.localizeHeader(): String =
            "resourcify.categories.$projectType.${header.lowercase().replace(" ", "_")}".localizeOrDefault(
                header.capitalizeAll()
            )
    }
}