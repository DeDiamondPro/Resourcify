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
import dev.dediamondpro.resourcify.util.PackUtils
import dev.dediamondpro.resourcify.util.Utils
import dev.dediamondpro.resourcify.util.getJson
import dev.dediamondpro.resourcify.util.postAndGetJson
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.ChatColor
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMinecraft
import net.minecraft.client.gui.GuiScreenResourcePacks
import org.apache.http.client.utils.URIBuilder
import java.awt.Color
import java.io.File
import java.net.URL
import java.util.concurrent.CompletableFuture

//#if MC >= 11904
//$$ import dev.dediamondpro.resourcify.mixins.ResourcePackOrganizerAccessor
//#endif

class UpdateGui(private val type: ApiInfo.ProjectType, private val folder: File) : PaginatedScreen() {
    private val hashes = CompletableFuture.supplyAsync {
        val files = PackUtils.getPackFiles(folder)
        files.associateBy { Utils.getSha512(it)!! }
    }
    private val updates = CompletableFuture.supplyAsync {
        getUpdates(type, hashes.get().keys.toList()).map { (k, v) -> v to k }.toMap()
    }
    private val mods = CompletableFuture.supplyAsync {
        val updates = updates.get()
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
    val packsToDelete = mutableListOf<File>()

    init {
        val checkingText = UIText("Checking for updates...").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = Color.YELLOW.toConstraint()
        } childOf window

        mods.whenComplete { mods, _ ->
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
                }.onMouseClick {
                    closeGui()
                    cleanUp()
                } childOf topBar
                UIText("${ChatColor.BOLD}Close").constrain {
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
                            updateText?.setText("${ChatColor.BOLD}Update All")
                            return@basicWidthConstraint 0f
                        }
                        val progress = (startSize - cards.size + cards.sumOf { card -> card.getProgress().toDouble() }
                            .toFloat()) / startSize
                        (1 - progress) * it.parent.getWidth()
                    }
                    height = 100.percent()
                } childOf updateAllButton
                updateText = UIText("${ChatColor.BOLD}Update All").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                } childOf updateAllButton
                topText = UIText("${mods.size} update${if (mods.size == 1) "" else "s"} available!").constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                } childOf topBar

                val scrollBox = ScrollComponent(pixelsPerScroll = 30f, scrollAcceleration = 1.5f).constrain {
                    x = 0.pixels()
                    y = 30.pixels()
                    width = 100.percent()
                    height = 100.percent() - y
                } childOf window
                val updateContainer = UIContainer().constrain {
                    x = 4.pixels()
                    width = 100.percent() - 8.pixels()
                    height = ChildLocationSizeConstraint() + 4.pixels()
                } childOf scrollBox

                cards.addAll(mods.map { (project, newVersion) ->
                    UpdateCard(project, newVersion, hashes.get()[updates.get()[newVersion]]!!, this).constrain {
                        y = SiblingConstraint(padding = 2f)
                        width = 100.percent()
                    } childOf updateContainer
                })

                updateAllButton.onMouseClick {
                    if (startSize != 0) return@onMouseClick
                    startSize = cards.size
                    cards.forEach { it.downloadUpdate() }
                    updateText.setText("${ChatColor.BOLD}Updating...")
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
            topText?.setText("${cards.size} update${if (cards.size == 1) "" else "s"} available!")
        }
    }

    private fun closeGui() {
        if (selectedUpdates.isNotEmpty()) return
        if (reloadOnClose) {
            Platform.reloadResources()
            UMinecraft.getMinecraft().gameSettings.saveOptions()
        }
        val screen = backScreens.firstOrNull { it is GuiScreenResourcePacks }
        if (screen == null) {
            displayScreen(null)
        } else {
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
        cleanUp()
        packsToDelete.forEach {
            if (!it.delete()) println("Failed to delete old resource pack file.")
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
                    if (allHashes.contains(updates[hash]!!.primaryFile?.hashes?.sha512)) null else updates[hash]
                } else {
                    null
                }
            }
        }
    }
}