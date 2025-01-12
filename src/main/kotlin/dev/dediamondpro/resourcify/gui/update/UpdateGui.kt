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

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.update.components.UpdateCard
import dev.dediamondpro.resourcify.mixins.PackScreenAccessor
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.services.*
import dev.dediamondpro.resourcify.util.PackUtils
import dev.dediamondpro.resourcify.util.localize
import dev.dediamondpro.resourcify.util.markdown
import dev.dediamondpro.resourcify.util.supplyAsync
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
import java.awt.Color
import java.io.File
import java.util.concurrent.CompletableFuture

//#if MC >= 11904
//$$ import dev.dediamondpro.resourcify.mixins.ResourcePackOrganizerAccessor
//#endif

class UpdateGui(val type: ProjectType, private val folder: File) : PaginatedScreen() {
    private val cards = mutableListOf<UpdateCard>()
    private var topText: UIText? = null
    private var updateText: UIText? = null
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
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, (-(this@UpdateGui as GuiScreen).width).pixels())
    }.animateAfterUnhide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, 4.pixels())
    } childOf scrollBox
    private val changelogContainer = UIBlock(Color(0, 0, 0, 100)).constrain {
        x = (this@UpdateGui as GuiScreen).width.pixels()
        width = 100.percent() - 8.pixels()
        height = ChildLocationSizeConstraint() + 4.pixels()
    }.animateBeforeHide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, (this@UpdateGui as GuiScreen).width.pixels())
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

        getUpdates().exceptionally {
            it.printStackTrace()
            emptyMap()
        }.thenAccept { projects ->
            Window.enqueueRenderOperation {
                checkingText.hide(true)
                if (projects == null) return@enqueueRenderOperation
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
                } childOf updateAllButton
                updateText = UIText().constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                } childOf updateAllButton
                topText = UIText().constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                } childOf topBar

                cards.addAll(projects.map { (file, data) ->
                    UpdateCard(data, file, this).constrain {
                        y = SiblingConstraint(padding = 2f)
                        width = 100.percent()
                    } childOf updateContainer
                })

                updateAllButton.onMouseClick {
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
                    it.printStackTrace()
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
            updateText?.setText("${ChatColor.BOLD}${"resourcify.updates.update_all".localize()}")
        } else {
            updateText?.setText("${ChatColor.BOLD}${"resourcify.updates.up-to-date".localize()}")
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

    fun showChangeLog(project: IProject, version: IVersion, updateButton: UIComponent) {
        updateContainer.hide()
        changelogContainer.constrain { x = (this@UpdateGui as GuiScreen).width.pixels() }
        changelogContainer.clearChildren()
        UIText("resourcify.updates.updates".localize()).constrain {
            x = 4.pixels()
            y = 8.pixels()
            color = Color(65, 105, 225).toConstraint()
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
        if (reloadOnClose) {
            Platform.reloadResources()
            UMinecraft.getMinecraft().gameSettings.saveOptions()
        }
        val screen = backScreens.lastOrNull { it !is PaginatedScreen }
        if (screen == null) {
            displayScreen(null)
        } else {
            when (type) {
                ProjectType.RESOURCE_PACK -> {
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
                ProjectType.AYCY_RESOURCE_PACK -> {
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

    data class UpdateData(val project: IProject, val version: IVersion)
}