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

package dev.dediamondpro.resourcify.gui.browsepage

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.constraints.MaxComponentConstraint
import dev.dediamondpro.resourcify.constraints.WindowMinConstraint
import dev.dediamondpro.resourcify.elements.DropDown
import dev.dediamondpro.resourcify.elements.Paginator
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.browsepage.components.ResourceCard
import dev.dediamondpro.resourcify.modrinth.ApiInfo
import dev.dediamondpro.resourcify.modrinth.Categories
import dev.dediamondpro.resourcify.modrinth.GameVersions
import dev.dediamondpro.resourcify.modrinth.SearchResponse
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.util.getJson
import dev.dediamondpro.resourcify.util.localize
import dev.dediamondpro.resourcify.util.supplyAsync
import gg.essential.elementa.components.*
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.universal.UMatrixStack
import net.minecraft.client.gui.GuiScreen
import org.apache.http.client.utils.URIBuilder
import java.awt.Color
import java.io.File
import java.util.concurrent.CompletableFuture
import kotlin.math.ceil

class BrowseScreen(private val type: ApiInfo.ProjectType, private val downloadFolder: File) : PaginatedScreen() {

    private var offset = 0
    private val selectedCategories = mutableListOf<Categories>()
    private var fetchingFuture: CompletableFuture<SearchResponse?>? = null
    private var totalHits: Int = 0

    private val contentBox = UIContainer().constrain {
        x = CenterConstraint()
        y = 4.pixels()
        width = ChildBasedSizeConstraint(padding = 4f)
        height = 100.percent()
    } childOf window

    private val sideContainer = UIContainer().constrain {
        x = 0.pixels()
        y = 0.pixels()
        width = 160.pixels()
        height = 100.percent()
    } childOf contentBox

    private val sideBoxScrollable = ScrollComponent(pixelsPerScroll = 30f, scrollAcceleration = 1.5f).constrain {
        x = 0.pixels()
        y = 33.pixels()
        width = 160.pixels()
        height = 100.percent() - 37.pixels()
    } childOf sideContainer

    private val categoryContainer = UIBlock(color = Color(0, 0, 0, 100)).constrain {
        x = 0.pixels()
        y = 0.pixels()
        width = 160.pixels()
        height = ChildLocationSizeConstraint()
    } childOf sideBoxScrollable

    private val mainBox = UIContainer().constrain {
        x = 0.pixels(alignOpposite = true)
        y = 0.pixels()
        width = WindowMinConstraint(528.pixels())
        height = 100.percent()
    } childOf contentBox

    private val headerBox = UIBlock(color = Color(0, 0, 0, 100)).constrain {
        x = 0.pixels()
        y = 0.pixels()
        width = 100.percent()
        height = 29.pixels()
    } childOf mainBox

    private lateinit var searchBox: UITextInput

    private val projectScrollable = ScrollComponent(pixelsPerScroll = 30f, scrollAcceleration = 1.5f).constrain {
        x = 0.pixels()
        y = 33.pixels()
        width = 100.percent()
        height = 100.percent() - 37.pixels()
    } childOf mainBox

    private val projectContainer = UIContainer().constrain {
        x = 0.pixels()
        y = SiblingConstraint(padding = 4f)
        width = 100.percent()
        height = ChildLocationSizeConstraint()
    } childOf projectScrollable

    private var versionDropDown: DropDown? = null
    private var sortDropDown: DropDown? = null

    init {
        sideBar()
        header()
        loadPacks()
    }

    private fun sideBar() {
        Paginator(this).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 160.pixels()
            height = 29.pixels()
        } childOf sideContainer

        val categoriesBox = UIContainer().constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        } childOf categoryContainer
        Categories.getCategoriesByHeaderWhenLoaded({ it.projectType == type.projectType }) { categoriesHeaders ->
            for ((header, categories) in categoriesHeaders) {
                UIText(header).constrain {
                    x = 4.pixels()
                    y = MaxConstraint(4.pixels(), SiblingConstraint(padding = 4f))
                    textScale = 1.5f.pixels()
                } childOf categoriesBox

                for (category in categories) {
                    val checkBox = UIContainer().constrain {
                        x = 0.pixels()
                        y = 0.pixels()
                        width = 7.pixels()
                        height = 7.pixels()
                    } effect OutlineEffect(Color.LIGHT_GRAY, 1f)

                    val check = UIBlock(Color(192, 192, 192, 0)).constrain {
                        x = 1.pixels()
                        y = 1.pixels()
                        width = 5.pixels()
                        height = 5.pixels()
                    } childOf checkBox

                    val categoryBox = UIContainer().constrain {
                        x = 4.pixels()
                        y = SiblingConstraint(4f)
                        width = ChildBasedSizeConstraint(4f)
                        height = ChildBasedMaxSizeConstraint()
                    }.onMouseClick {
                        if (it.mouseButton != 0) return@onMouseClick
                        if (selectedCategories.contains(category)) {
                            selectedCategories.remove(category)
                            check.animate {
                                setColorAnimation(
                                    Animations.IN_OUT_QUAD,
                                    0.15f,
                                    Color(192, 192, 192, 0).toConstraint(),
                                    0f
                                )
                            }
                        } else {
                            selectedCategories.add(category)
                            check.animate {
                                setColorAnimation(
                                    Animations.IN_OUT_QUAD,
                                    0.15f,
                                    Color(192, 192, 192, 255).toConstraint(),
                                    0f
                                )
                            }
                        }
                        loadPacks()
                    } childOf categoriesBox
                    checkBox childOf categoryBox

                    UIText(category.localizedName).constrain {
                        x = SiblingConstraint(padding = 4f)
                        y = 0.pixels()
                        color = Color.LIGHT_GRAY.toConstraint()
                    } childOf categoryBox
                }
            }
        }
        val versionsBox = UIContainer().constrain {
            x = 0.pixels()
            y = SiblingConstraint()
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        } childOf categoryContainer
        GameVersions.getVersionsWhenLoaded {
            UIText("resourcify.browse.minecraft_version".localize()).constrain {
                x = 4.pixels()
                y = 0.pixels()
                textScale = 1.5f.pixels()
            } childOf versionsBox
            val currVersion = Platform.getMcVersion()
            versionDropDown = DropDown(
                it.filter { version -> version.versionType == "release" }
                    .map { version -> version.version }.reversed(),
                selectedOptions = if (it.any { version -> version.version == currVersion })
                    mutableListOf(currVersion) else mutableListOf(),
                top = true, placeHolder = "resourcify.browse.minecraft_version.placeholder".localize()
            ).constrain {
                x = 4.pixels()
                y = SiblingConstraint(padding = 4f)
                width = 100.percent() - 8.pixels()
            }.onSelectionUpdate {
                loadPacks()
            } childOf versionsBox
        }
    }

    private fun header() {
        searchBox = (UITextInput("resourcify.browse.search".localize(type.displayName.localize())).constrain {
            x = 6.pixels()
            y = CenterConstraint()
            width = 100.percent() - 89.pixels()
        }.onUpdate {
            loadPacks()
        }.onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            grabWindowFocus()
        } childOf headerBox) as UITextInput
        sortDropDown = DropDown(
            listOf(
                "resourcify.browse.sort.relevance".localize(),
                "resourcify.browse.sort.downloads".localize(),
                "resourcify.browse.sort.follows".localize(),
                "resourcify.browse.sort.newest".localize(),
                "resourcify.browse.sort.updated".localize()
            ),
            onlyOneOption = true,
            selectedOptions = mutableListOf("resourcify.browse.sort.relevance".localize())
        ).constrain {
            x = 5.pixels(true)
            y = CenterConstraint()
            width = 72.pixels()
        }.onSelectionUpdate {
            loadPacks()
        } childOf headerBox
    }

    private fun loadPacks(clear: Boolean = true) {
        fetchingFuture?.cancel(true)
        fetchingFuture = supplyAsync {
            if (clear) offset = 0
            else offset += 20
            val sortType = when (sortDropDown!!.options.indexOf(sortDropDown!!.selectedOptions.first())) {
                0 -> "relevance"
                1 -> "downloads"
                2 -> "follows"
                3 -> "newest"
                4 -> "updated"
                else -> "relevance"
            }
            val url = URIBuilder("${ApiInfo.API}/search")
                .setParameter("query", searchBox.getText())
                .setParameter("facets", buildFacets())
                .setParameter("limit", "20")
                .setParameter("offset", "$offset")
                .setParameter("index", sortType)
                .build()
            url.toURL().getJson<SearchResponse>()
        }.whenComplete { response, error ->
            if (error != null) return@whenComplete
            totalHits = response?.totalHits ?: 0
            val projects = response?.hits ?: return@whenComplete
            Window.enqueueRenderOperation {
                if (clear) projectContainer.clearChildren()

                for (i in 0 until ceil(projects.size / 2f).toInt()) {
                    val row = UIContainer().constrain {
                        x = 0.pixels()
                        y = SiblingConstraint(padding = 4f)
                        width = 100.percent()
                        height = ChildBasedMaxSizeConstraint()
                    } childOf projectContainer
                    val constraint = MaxComponentConstraint(ChildLocationSizeConstraint() + 4.pixels())
                    ResourceCard(projects[i * 2], type, downloadFolder).constrain {
                        x = 0.pixels()
                        y = 0.pixels()
                        width = 50.percent() - 2.pixels()
                        height = constraint
                    } childOf row
                    if (projects.size > i * 2 + 1) ResourceCard(projects[i * 2 + 1], type, downloadFolder).constrain {
                        x = 0.pixels(true)
                        y = 0.pixels()
                        width = 50.percent() - 2.pixels()
                        height = constraint.createChildConstraint(ChildLocationSizeConstraint() + 4.pixels())
                    } childOf row
                }

                if (clear) projectScrollable.scrollToTop(false)
                fetchingFuture = null
            }
        }
    }

    private fun buildFacets(): String = buildString {
        append("[${type.searchFacet}")
        if (selectedCategories.isNotEmpty()) append(",")
        append(selectedCategories.joinToString(",") { "[\"categories:'${it.name}'\"]" })
        versionDropDown?.selectedOptions?.let {
            if (it.isEmpty()) return@let
            append(",[")
            append(it.joinToString(separator = ",") { version -> "\"versions:$version\"" })
            append("]")
        } ?: run {
            val currVersion = Platform.getMcVersion()
            if (GameVersions.getVersions().any { it.version == currVersion })
                append(",[\"versions:${Platform.getMcVersion()}\"]")
        }
        append("]")
    }

    override fun onDrawScreen(matrixStack: UMatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (projectScrollable.verticalOffset + projectScrollable.verticalOverhang < 150 && fetchingFuture == null &&
            offset + 20 < totalHits
        ) {
            loadPacks(false)
        }
        super.onDrawScreen(matrixStack, mouseX, mouseY, partialTicks)
    }

    override fun goBack() {
        //#if MC == 10809
        if (type == ApiInfo.ProjectType.AYCY_RESOURCE_PACK) {
            val screen = backScreens.lastOrNull()
            val previousScreenField = screen?.javaClass?.getDeclaredField("previousScreen")
            previousScreenField?.isAccessible = true
            displayScreen(previousScreenField?.get(screen) as GuiScreen?)
            cleanUp()
            return
        }
        //#endif
        super.goBack()
    }
}