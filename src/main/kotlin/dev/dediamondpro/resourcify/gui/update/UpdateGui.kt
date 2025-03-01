/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2025 DeDiamondPro
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

import dev.dediamondpro.resourcify.Constants
import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.gui.update.components.UpdateCard
import dev.dediamondpro.resourcify.services.*
import dev.dediamondpro.resourcify.util.PackUtils
import dev.dediamondpro.resourcify.util.localize
import dev.dediamondpro.resourcify.util.markdown
import dev.dediamondpro.resourcify.util.supplyAsync
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.MinConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.ChatColor
import gg.essential.universal.UKeyboard
import java.awt.Color
import java.io.File
import java.util.concurrent.CompletableFuture

class UpdateGui(val type: ProjectType, private val folder: File) : PaginatedScreen(adaptScale = false) {
    private val cards = mutableListOf<UpdateCard>()
    private var topText: UIText? = null
    private var updateAllButton: UIBlock? = null
    private var updateText: UIText? = null
    private var startSize = 0
    private val selectedUpdates = mutableListOf<UpdateCard>()
    private var closing = false
    val packsToDelete = mutableListOf<File>()

    private val scrollBox = ScrollComponent(pixelsPerScroll = 30f, scrollAcceleration = 1.5f).constrain {
        x = 0.pixels()
        y = 30.pixels()
        width = 100.percent()
        height = 100.percent() - y
    } childOf window

    private val contentContainer = UIContainer().constrain {
        x = CenterConstraint()
        width = MinConstraint(692.pixels(), 100.percent() - 8.pixels)
        height = ChildBasedMaxSizeConstraint() + 4.pixels()
    } effect ScissorEffect() childOf scrollBox

    private val updateContainer = UIContainer().constrain { // Used for update cards
        width = 100.percent()
        height = ChildLocationSizeConstraint()
    }.animateBeforeHide {
        setXAnimation(
            Animations.IN_OUT_QUAD,
            0.15f,
            basicXConstraint { contentContainer.getLeft() - contentContainer.getWidth() })
    }.animateAfterUnhide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.15f, 0.pixels())
    } childOf contentContainer

    private val changelogContainer = UIBlock(Colors.BACKGROUND).constrain { // Used for changelogs
        x = basicXConstraint { contentContainer.getRight() }
        width = 100.percent()
        height = ChildLocationSizeConstraint() + 4.pixels()
    }.animateBeforeHide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.15f, basicXConstraint { contentContainer.getRight() })
    }.animateAfterUnhide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.15f, 0.pixels())
    } childOf contentContainer

    private val stopCloseBox = UIBlock(Colors.FULLSCREEN_BACKGROUND).constrain {
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
            color = Colors.TEXT_WARN.toConstraint()
        } childOf stopCloseBox
        stopCloseBox.hide(true)

        val checkingText = UIText("resourcify.updates.checking".localize()).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = 2.pixels()
            color = Colors.TEXT_WARN.toConstraint()
        } childOf window

        getUpdates().exceptionally {
            Constants.LOGGER.error("Failed to fetch updates", it)
            emptyMap()
        }.thenAccept { projects ->
            Window.enqueueRenderOperation {
                checkingText.hide(true)
                if (projects == null) return@enqueueRenderOperation
                val topBar = UIContainer().constrain {
                    x = CenterConstraint()
                    y = 4.pixels()
                    width = MinConstraint(692.pixels(), 100.percent() - 8.pixels)
                    height = 22.pixels()
                } childOf window
                val closeButton = UIBlock(Colors.BUTTON_SECONDARY).constrain {
                    y = 0.pixels()
                    x = 0.pixels()
                    width = 73.pixels()
                    height = 100.percent()
                }.onMouseClick { closeGui() } childOf topBar
                UIText("${ChatColor.BOLD}${localize("resourcify.screens.close")}").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    color = Colors.TEXT_PRIMARY.toConstraint()
                } childOf closeButton
                updateAllButton =
                    UIBlock(if (projects.isNotEmpty()) Colors.BUTTON_PRIMARY else Colors.BUTTON_PRIMARY_DISABLED).constrain {
                        y = 0.pixels()
                        x = 4.pixels(true)
                        width = 73.pixels()
                        height = 100.percent()
                    } childOf topBar
                val progressBox = UIBlock(Color(0, 0, 0, 100)).constrain {
                    x = 0.pixels(true)
                    y = 0.pixels()
                    width = basicWidthConstraint {
                        if (startSize == 0) return@basicWidthConstraint 0f
                        val updateCount = cards.count { card -> card.hasUpdate() }
                        if (updateCount == 0) {
                            startSize = 0
                            updateText()
                            return@basicWidthConstraint 0f
                        }
                        val progress = (startSize - updateCount + cards.sumOf { card -> card.getProgress().toDouble() }
                            .toFloat()) / startSize
                        return@basicWidthConstraint (1 - progress) * it.parent.getWidth()
                    }
                    height = 100.percent()
                } childOf updateAllButton!!
                updateText = UIText().constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    color = Colors.TEXT_PRIMARY.toConstraint()
                } childOf updateAllButton!!
                topText = UIText().constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    color = Colors.TEXT_PRIMARY.toConstraint()
                } childOf topBar

                cards.addAll(projects.map { (file, data) ->
                    UpdateCard(data, file, this).constrain {
                        y = SiblingConstraint(padding = 2f)
                        width = 100.percent()
                    } childOf updateContainer
                })

                updateAllButton!!.onMouseClick {
                    val updateCount = cards.count { it.hasUpdate() }
                    if (startSize != 0 || updateCount == 0) return@onMouseClick
                    startSize = updateCount
                    cards.forEach { it.downloadUpdate() }
                    updateText?.setText("${ChatColor.BOLD}${localize("resourcify.updates.updating")}")
                    progressBox.constraints.width.recalculate = true
                }

                updateText()
            }
        }
    }

    private fun getUpdates(): CompletableFuture<Map<File, Map<IService, UpdateData?>>> {
        val files = PackUtils.getPackFiles(folder)

        // Fetch updates from each service
        val futures = mutableMapOf<IService, CompletableFuture<Map<File, UpdateData?>>>()
        for (service in ServiceRegistry.getAllServices()) {
            val future = service.getUpdates(files, type).thenApply { updates ->
                val ids = updates.values.filterNotNull().map { it.getProjectId() }
                val projects = if (ids.isEmpty()) {
                    emptyMap()
                } else {
                    service.getProjectsFromIds(ids)
                }
                // Add project into map
                return@thenApply updates.filter { it.value == null || projects.containsKey(it.value!!.getProjectId()) }
                    .map {
                        it.key to if (it.value == null) null else UpdateData(
                            projects[it.value!!.getProjectId()]!!,
                            it.value!!
                        )
                    }
                    .toMap()
            }
            futures[service] = future
        }

        // Aggregate results
        return supplyAsync {
            val updates = mutableMapOf<File, MutableMap<IService, UpdateData?>>()
            for ((source, future) in futures) {
                val result = future.exceptionally {
                    Constants.LOGGER.error("Failed to fetch updates from \"$source\"", it)
                    emptyMap()
                }.get()
                for ((file, project) in result) {
                    if (!updates.containsKey(file)) {
                        updates[file] = mutableMapOf()
                    }
                    updates[file]!![source] = project
                }
            }
            // Do not include it if it is up to date at every available service
            return@supplyAsync updates.filter { it.value.values.any { data -> data != null } }
        }
    }

    fun updateText() {
        val updateCount = cards.count { it.hasUpdate() }
        topText?.setText(
            localize(
                "resourcify.updates.updates_available", updateCount,
                localize(if (updateCount == 1) "resourcify.updates.update_singular" else "resourcify.updates.update_plural")
            )
        )
        if (startSize != 0) return // There are currently updates being downloaded
        if (updateCount != 0) {
            updateAllButton?.setColor(Colors.BUTTON_PRIMARY.toConstraint())
            updateText?.setText("${ChatColor.BOLD}${"resourcify.updates.update_all".localize()}")
        } else {
            updateAllButton?.setColor(Colors.BUTTON_PRIMARY_DISABLED.toConstraint())
            updateText?.setText("${ChatColor.BOLD}${"resourcify.updates.up-to-date".localize()}")
        }
    }

    fun registerUpdate(updateCard: UpdateCard) {
        selectedUpdates.add(updateCard)
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

    fun showChangeLog(project: IProject, version: IVersion, updateButton: UIComponent) {
        updateContainer.hide()
        changelogContainer.clearChildren()
        UIText("resourcify.updates.updates".localize()).constrain {
            x = 4.pixels()
            y = 8.pixels()
            color = Colors.TEXT_LINK.toConstraint()
        }.onMouseClick {
            changelogContainer.hide()
            updateContainer.unhide()
        } childOf changelogContainer
        UIText("> ${project.getName()} > ${version.getVersionNumber() ?: version.getName()}").constrain {
            x = SiblingConstraint()
            y = 8.pixels()
        } childOf changelogContainer
        updateButton.constrain {
            x = 4.pixels(true)
            y = 4.pixels()
            height = 22.pixels()
        }.onMouseClick {
            changelogContainer.hide()
            updateContainer.unhide()
        } childOf changelogContainer
        version.getChangeLog().thenApply {
            Window.enqueueRenderOperation {
                markdown(it).constrain {
                    x = 4.pixels()
                    y = SiblingConstraint(4f)
                    width = 100.percent() - 8.pixels()
                } childOf changelogContainer
            }
        }
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
        val screen = backScreens.lastOrNull { it !is PaginatedScreen }
        if (screen == null) {
            displayScreen(null)
        } else {
            displayScreen(screen)
        }
        cleanUp()
        packsToDelete.forEach {
            if (!it.delete()) {
                Constants.LOGGER.warn("Failed to delete '$it'.")
            }
        }
    }

    override fun onKeyPressed(keyCode: Int, typedChar: Char, modifiers: UKeyboard.Modifiers?) {
        if (window.focusedComponent == null && keyCode == UKeyboard.KEY_ESCAPE) {
            closeGui()
        } else {
            super.onKeyPressed(keyCode, typedChar, modifiers)
        }
    }

    data class UpdateData(val project: IProject, val version: IVersion)
}