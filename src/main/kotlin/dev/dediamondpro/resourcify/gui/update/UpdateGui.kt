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

package dev.dediamondpro.resourcify.gui.update

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.update.components.UpdateCard
import dev.dediamondpro.resourcify.mixins.PackScreenAccessor
import dev.dediamondpro.resourcify.modrinth.ApiInfo
import dev.dediamondpro.resourcify.modrinth.ModrinthUpdateFormat
import dev.dediamondpro.resourcify.modrinth.ProjectResponse
import dev.dediamondpro.resourcify.modrinth.Version
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.util.*
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.universal.ChatColor
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.GuiScreen
import org.apache.http.client.utils.URIBuilder
import java.awt.Color
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture

//#if MC >= 11904
//$$ import dev.dediamondpro.resourcify.mixins.ResourcePackOrganizerAccessor
//#endif

class UpdateGui(val type: ApiInfo.ProjectType, private val folder: File) : PaginatedScreen() {
    private val hashes = CompletableFuture.supplyAsync {
        val files = PackUtils.getPackFiles(folder)
        files.associateBy { Utils.getSha512(it)!! }
    }
    private val updates = CompletableFuture.supplyAsync {
        getUpdates(type, hashes.get().keys.toList()).map { (k, v) -> v to k }.toMap()
    }
    private val mods = updates.thenApplyAsync { updates ->
        if (updates.isEmpty()) return@thenApplyAsync emptyMap()
        val idString = updates.keys.joinToString(",", "[", "]") { "\"${it.projectId}\"" }
        URIBuilder("${ApiInfo.API}/projects").setParameter("ids", idString)
            .build().toURL().getJson<List<ProjectResponse>>()!!
            .map { project -> project to updates.keys.first { it.projectId == project.id } }
            .sortedBy { (_, newVersion) ->
                if (Platform.getSelectedResourcePacks().contains(hashes.get()[updates[newVersion]]!!)) 0
                else 1
            }.toMap()
    }
    private val cards = mutableListOf<UpdateCard>()
    private var topText: UIText? = null
    private var startSize = 0
    private val selectedUpdates = mutableListOf<UpdateCard>()
    private var reloadOnClose = false
    private var closing = false
    val packsToDelete = mutableListOf<File>()

    private val scrollBox = ScrollComponent(pixelsPerScroll = 30f, scrollAcceleration = 1.5f).constrain {
        x = 0.pixels()
        y = 30.pixels()
        width = 100.percent()
        height = 100.percent() - y
    } childOf window
    private val updateContainer = UIContainer().constrain {
        x = 4.pixels()
        width = 100.percent() - 8.pixels()
        height = ChildLocationSizeConstraint() + 4.pixels()
    }.animateBeforeHide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, (-this@UpdateGui.width).pixels())
    }.animateAfterUnhide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, 4.pixels())
    } childOf scrollBox
    private val changelogContainer = UIBlock(Color(0, 0, 0, 100)).constrain {
        x = this@UpdateGui.width.pixels()
        width = 100.percent() - 8.pixels()
        height = ChildLocationSizeConstraint() + 4.pixels()
    }.animateBeforeHide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, this@UpdateGui.width.pixels())
    }.animateAfterUnhide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, 4.pixels())
    } childOf scrollBox

    private val stopCloseBox = UIBlock(color = Color(0, 0, 0, 150)).constrain {
        x = 0.pixels()
        y = 0.pixels()
        width = 100.percent()
        height = 100.percent()
    }.onFocusLost {
        hide(true)
    }.onKeyType { _, keyCode ->
        if (keyCode != UKeyboard.KEY_ESCAPE) return@onKeyType
        hide(true)
        releaseWindowFocus()
    } childOf window

    init {
        changelogContainer.hide(true)
        UIText("resourcify.updates.wait".localize()).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = 2.pixels()
            color = Color.YELLOW.toConstraint()
        } childOf stopCloseBox
        stopCloseBox.hide(true)

        val checkingText = UIText("resourcify.updates.checking".localize()).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = 2.pixels()
            color = Color.YELLOW.toConstraint()
        } childOf window

        mods.whenComplete { projects, _ ->
            Window.enqueueRenderOperation {
                checkingText.hide(true)
                val topBar = UIContainer().constrain {
                    x = 4.pixels()
                    y = 4.pixels()
                    width = 100.percent() - 8.pixels()
                    height = 22.pixels()
                } childOf window
                val closeButton = UIBlock(Color(0, 0, 0, 100)).constrain {
                    y = 0.pixels()
                    x = 0.pixels()
                    width = 73.pixels()
                    height = 100.percent()
                }.onMouseClick { closeGui() } childOf topBar
                UIText("${ChatColor.BOLD}${localize("resourcify.screens.close")}").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                } childOf closeButton
                val updateAllButton = UIBlock(Color(27, 217, 106)).constrain {
                    y = 0.pixels()
                    x = 4.pixels(true)
                    width = 73.pixels()
                    height = 100.percent()
                } childOf topBar
                var updateText: UIText? = null
                val progressBox = UIBlock(Color(0, 0, 0, 100)).constrain {
                    x = 0.pixels(true)
                    y = 0.pixels()
                    width = basicWidthConstraint {
                        if (startSize == 0) return@basicWidthConstraint 0f
                        if (cards.isEmpty()) {
                            startSize = 0
                            updateText?.setText("${ChatColor.BOLD}${localize("resourcify.updates.update_all")}")
                            return@basicWidthConstraint 0f
                        }
                        val progress = (startSize - cards.size + cards.sumOf { card -> card.getProgress().toDouble() }
                            .toFloat()) / startSize
                        (1 - progress) * it.parent.getWidth()
                    }
                    height = 100.percent()
                } childOf updateAllButton
                updateText = UIText("${ChatColor.BOLD}${localize("resourcify.updates.update_all")}").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                } childOf updateAllButton
                topText = UIText(
                    localize(
                        "resourcify.updates.updates_available",
                        projects.size,
                        localize(if (projects.size == 1) "resourcify.updates.update_singular" else "resourcify.updates.update_plural")
                    )
                ).constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                } childOf topBar

                cards.addAll(projects.map { (project, newVersion) ->
                    UpdateCard(project, newVersion, hashes.get()[updates.get()[newVersion]]!!, this).constrain {
                        y = SiblingConstraint(padding = 2f)
                        width = 100.percent()
                    } childOf updateContainer
                })

                updateAllButton.onMouseClick {
                    if (startSize != 0) return@onMouseClick
                    startSize = cards.size
                    cards.forEach { it.downloadUpdate() }
                    updateText.setText("${ChatColor.BOLD}${localize("resourcify.updates.updating")}")
                    progressBox.constraints.width.recalculate = true
                }
            }
        }
    }

    fun registerUpdate(updateCard: UpdateCard, reload: Boolean) {
        selectedUpdates.add(updateCard)
        if (reload) reloadOnClose = true
    }

    fun cancelUpdate(updateCard: UpdateCard) {
        selectedUpdates.remove(updateCard)
    }

    fun removeCard(updateCard: UpdateCard) {
        Window.enqueueRenderOperation {
            updateCard.hide()
            cards.remove(updateCard)
            selectedUpdates.remove(updateCard)
            topText?.setText(
                localize(
                    "resourcify.updates.updates_available",
                    cards.size,
                    localize(if (cards.size == 1) "resourcify.updates.update_singular" else "resourcify.updates.update_plural")
                )
            )
        }
    }

    fun showChangeLog(project: ProjectResponse, version: Version, updateButton: UIComponent) {
        updateContainer.hide()
        changelogContainer.constrain { x = this@UpdateGui.width.pixels() }
        changelogContainer.clearChildren()
        UIText("resourcify.updates.updates".localize()).constrain {
            x = 4.pixels()
            y = 8.pixels()
            color = Color(65, 105, 225).toConstraint()
        }.onMouseClick {
            changelogContainer.hide()
            updateContainer.unhide()
        } childOf changelogContainer
        UIText("> ${project.title} > ${version.versionNumber}").constrain {
            x = SiblingConstraint()
            y = 8.pixels()
        } childOf changelogContainer
        updateButton.constrain {
            x = 4.pixels(true)
            y = 4.pixels()
            height = 22.pixels()
        } childOf changelogContainer
        markdown(version.changelog).constrain {
            x = 4.pixels()
            y = SiblingConstraint(4f)
            width = 100.percent() - 8.pixels()
        } childOf changelogContainer
        changelogContainer.unhide()
    }

    private fun closeGui() {
        if (closing) return
        if (selectedUpdates.isNotEmpty()) {
            Window.enqueueRenderOperation {
                stopCloseBox.unhide(false)
                stopCloseBox.grabWindowFocus()
            }
            return
        }
        closing = true // Prevent some button mashing issues
        if (reloadOnClose) {
            Platform.reloadResources()
            UMinecraft.getMinecraft().gameSettings.saveOptions()
        }
        val screen = backScreens.firstOrNull { it !is PaginatedScreen }
        if (screen == null) {
            displayScreen(null)
        } else {
            when (type) {
                ApiInfo.ProjectType.RESOURCE_PACK -> {
                    //#if MC >= 11904
                    //$$ if (reloadOnClose) {
                    //$$     ((screen as PackScreenAccessor).organizer as ResourcePackOrganizerAccessor).applier.accept(UMinecraft.getMinecraft().resourcePackManager)
                    //$$ } else {
                    //$$     displayScreen(screen)
                    //$$ }
                    //#else
                    displayScreen(if (reloadOnClose) (screen as PackScreenAccessor).parentScreen else screen)
                    //#endif
                }
                //#if MC == 10809
                ApiInfo.ProjectType.AYCY_RESOURCE_PACK -> {
                    val previousScreenField = screen.javaClass.getDeclaredField("previousScreen")
                    previousScreenField.isAccessible = true
                    displayScreen(if (reloadOnClose) previousScreenField.get(screen) as GuiScreen else screen)
                }
                //#endif
                else -> displayScreen(screen)
            }
        }
        cleanUp()
        packsToDelete.forEach {
            if (!it.delete()) println("Failed to delete old pack file.")
        }
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        if (window.focusedComponent == null && keyCode == UKeyboard.KEY_ESCAPE) {
            closeGui()
        } else {
            super.onKeyPressed(keyCode, typedChar, modifiers)
        }
    }

    companion object {
        private val updateInfo = mutableMapOf<String, Version?>()

        fun getUpdates(type: ApiInfo.ProjectType, hashes: List<String>): Map<String, Version> {
            fetchUpdates(type, hashes.filter { !updateInfo.containsKey(it) }, hashes)
            return hashes.filter { updateInfo[it] != null }.associateWith { updateInfo[it]!! }
        }

        private fun fetchUpdates(type: ApiInfo.ProjectType, hashes: List<String>, allHashes: List<String>) {
            if (hashes.isEmpty()) return
            val data = ModrinthUpdateFormat(loaders = listOf(type.loader), hashes = hashes)
            val updates: Map<String, Version> =
                URL("${ApiInfo.API}/version_files/update").postAndGetJson(data) ?: return
            hashes.forEach { hash ->
                updateInfo[hash] = if (updates.containsKey(hash)) {
                    if (allHashes.contains(updates[hash]!!.getPrimaryFile()?.hashes?.sha512)) null else updates[hash]
                } else {
                    null
                }
            }
        }
    }
}