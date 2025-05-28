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

package dev.dediamondpro.resourcify.gui.update.components

import dev.dediamondpro.resourcify.Constants
import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.elements.DropDown
import dev.dediamondpro.resourcify.elements.McImage
import dev.dediamondpro.resourcify.gui.update.UpdateGui
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.services.IProject
import dev.dediamondpro.resourcify.services.IService
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.handlers.IrisHandler
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.gui.data.Icons
import dev.dediamondpro.resourcify.mixins.PackScreenAccessor
import dev.dediamondpro.resourcify.util.*
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.components.Window
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.ChatColor
import net.minecraft.client.gui.screens.packs.PackSelectionModel
import net.minecraft.client.gui.screens.packs.PackSelectionScreen
import net.minecraft.server.packs.FilePackResources
import java.awt.Color
import java.io.File
import java.util.concurrent.locks.ReentrantLock


class UpdateCard(
    private val data: Map<IService, UpdateGui.UpdateData?>,
    val file: File,
    private val gui: UpdateGui
) : UIBlock(Colors.BACKGROUND) {
    private var selectedService: IService
    private var selectedData: UpdateGui.UpdateData?
    private var progressBox: UIBlock? = null
    private var text: UIText? = null

    init {
        constrain {
            height = 56.pixels()
        }

        selectedService = data.keys.firstOrNull { it.getName() == Config.instance.defaultService } ?: data.keys.first()
        selectedData = data[selectedService]
        createCard(getProject(), selectedData?.version)
    }

    private fun createCard(project: IProject, version: IVersion?) {
        val mainHolder = UIContainer().constrain {
            width = 100.percent() - 185.pixels()
            height = 56.pixels()
        } effect ScissorEffect() childOf this

        val iconUrl = project.getIconUrl()
        if (iconUrl == null) {
            McImage(Icons.DEFAULT_ICON)
        } else {
            UIImage.ofURLCustom(iconUrl)
        }.constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = 48.pixels()
            height = 48.pixels()
        } childOf mainHolder
        UIText(project.getName()).constrain {
            x = 56.pixels()
            y = 8.pixels()
            textScale = 2.pixels()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf mainHolder

        if (version == null) {
            UIText("resourcify.updates.up-to-date".localize()).constrain {
                x = 56.pixels()
                y = SiblingConstraint(padding = 4f)
                color = Colors.TEXT_WARN.toConstraint()
            } childOf mainHolder
        } else {
            UIText(version.getName()).constrain {
                x = 56.pixels()
                y = SiblingConstraint(padding = 4f)
                color = Colors.TEXT_PRIMARY.toConstraint()
            } childOf mainHolder
            val versionNumberHolder = UIContainer().constrain {
                x = 56.pixels()
                y = SiblingConstraint(padding = 4f)
            } childOf mainHolder
            UIText(version.getVersionType().localizedName.localize()).constrain {
                x = 0.pixels()
                y = 0.pixels()
                color = version.getVersionType().color.toConstraint()
            } childOf versionNumberHolder
            version.getVersionNumber()?.let {
                UIText(it).constrain {
                    x = SiblingConstraint(padding = 4f)
                    y = 0.pixels()
                    color = Colors.TEXT_PRIMARY.toConstraint()
                } childOf versionNumberHolder
            }
        }

        val buttonHolder = UIContainer().constrain {
            x = 4.pixels(true)
            y = 4.pixels()
            width = 73.pixels()
            height = 48.pixels()
        } childOf this

        createUpdateButton() childOf buttonHolder

        val changeLogButton =
            UIBlock(if (version != null) Colors.BUTTON_SECONDARY else Colors.BUTTON_SECONDARY_DISABLED).constrain {
                y = 0.pixels(true)
                width = 73.pixels()
                height = 50.percent() - 2.pixels()
            }.onMouseClick {
                if (version == null) return@onMouseClick
                gui.showChangeLog(project, version, createUpdateButton())
            } childOf buttonHolder
        UIText("${ChatColor.BOLD}${localize("resourcify.updates.changelog")}").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf changeLogButton

        val sourceHolder = UIContainer().constrain {
            x = 81.pixels(true)
            y = 4.pixels()
            width = 100.pixels()
            height = 48.pixels()
        } childOf this
        val sourceTextHolder = UIContainer().constrain {
            height = 50.percent()
        } childOf sourceHolder
        UIText("resourcify.updates.source".localize()).constrain {
            y = CenterConstraint()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf sourceTextHolder

        DropDown(
            data.keys.map { it.getName() }, onlyOneOption = true,
            selectedOptions = mutableListOf(selectedService.getName())
        ).onSelectionUpdate { newService ->
            val downloadUrl = selectedData?.version?.getDownloadUrl()
            if (downloadUrl != null && DownloadManager.getProgress(downloadUrl) != null) {
                // We are currently downloading an update, don't allow changes
                this.selectedOptions.clear()
                this.selectedOptions.add(selectedService.getName())
                this.updateText()
                return@onSelectionUpdate
            }

            selectedService = data.keys.firstOrNull { it.getName() == newService.first() } ?: return@onSelectionUpdate
            selectedData = data[selectedService]

            this@UpdateCard.clearChildren()
            createCard(getProject(), selectedData?.version)
            gui.updateText()
        }.constrain {
            x = 0.pixels()
            y = 1.pixels(true)
            width = 100.percent()
            height = 50.percent() - 4.pixels()
        } childOf sourceHolder
    }

    fun hasUpdate(): Boolean {
        return selectedData?.version?.getDownloadUrl() != null
    }

    private fun createUpdateButton(): UIComponent {
        val updateButton =
            UIBlock(if (selectedData != null) Colors.BUTTON_PRIMARY else Colors.BUTTON_PRIMARY_DISABLED).constrain {
                y = 0.pixels()
                width = 73.pixels()
                height = 50.percent() - 2.pixels()
            }.onMouseClick {
                if (selectedData == null) return@onMouseClick
                downloadUpdate()
            }
        text = UIText(
            "${ChatColor.BOLD}${
                localize(if (selectedData != null) "resourcify.updates.update" else "resourcify.updates.up-to-date")
            }"
        ).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf updateButton
        val downloadUrl = selectedData?.version?.getDownloadUrl() ?: return updateButton
        progressBox = UIBlock(Color(0, 0, 0, 100)).constrain {
            x = 0.pixels(true)
            y = 0.pixels()
            width = basicWidthConstraint {
                val progress = DownloadManager.getProgress(downloadUrl)
                if (progress == null) 0f
                else (1 - progress) * it.parent.getWidth()
            }
            height = 100.percent()
        } childOf updateButton
        return updateButton
    }

    fun downloadUpdate() {
        val updateUrl = selectedData?.version?.getDownloadUrl() ?: return
        val newVersion = selectedData?.version ?: return
        if (DownloadManager.getProgress(updateUrl) == null) {
            gui.registerUpdate(this)
            text?.setText("${ChatColor.BOLD}${localize("resourcify.updates.updating")}")
            var downloadFile = File(file.parentFile, newVersion.getFileName())
            if (downloadFile.exists()) {
                downloadFile = File(file.parentFile, Utils.incrementFileName(newVersion.getFileName()))
            }
            DownloadManager.download(
                downloadFile,
                newVersion.getSha1(), updateUrl
            ) {
                try {
                    // Try to update the pack if it is currently selected, not critical if it fails
                    when (gui.type) {
                        ProjectType.RESOURCE_PACK, ProjectType.DATA_PACK -> {
                            try {
                                updateResourcePackLock.lock()

                                PaginatedScreen.backScreens.firstOrNull { it is PackSelectionScreen }?.let {
                                    val model = (it as PackScreenAccessor).model

                                    model.findNewPacks()

                                    // Find the original entry
                                    var isSelected = false
                                    var index = 0
                                    for (entry in model.selected) {
                                        if (entry !is PackSelectionModel.EntryBase) {
                                            continue
                                        }
                                        val entryFile = Platform.getFileFromPackResourceSupplier(entry.pack.resources)
                                        if (entryFile == null || entryFile != file) {
                                            continue
                                        }
                                        isSelected = true
                                        index = entry.selfList.indexOf(entry.pack)
                                        entry.unselect()
                                        break
                                    }

                                    // Find the new entry and update accordingly if needed
                                    if (isSelected) for (entry in model.unselected) {
                                        if (entry !is PackSelectionModel.EntryBase) {
                                            continue
                                        }
                                        val entryFile = Platform.getFileFromPackResourceSupplier(entry.pack.resources)
                                        if (entryFile == null || entryFile != downloadFile) {
                                            continue
                                        }
                                        // We have found the pack we downloaded, now select it
                                        entry.select()
                                        // Set the index correctly
                                        entry.otherList.remove(entry.pack)
                                        entry.otherList.add(index, entry.pack)
                                        model.onListChanged.run()
                                        break
                                    }
                                }
                            } finally {
                                updateResourcePackLock.unlock()
                            }
                        }

                        ProjectType.IRIS_SHADER -> {
                            PaginatedScreen.backScreens.firstOrNull { it !is PaginatedScreen }?.let {
                                if (IrisHandler.getActiveShader(it) == file.name) {
                                    Window.enqueueRenderOperation { IrisHandler.applyShaders(it, downloadFile.name) }
                                }
                            }
                        }

                        else -> {
                            // Other types (Optifine shaders) don't have an implementation to update the selected shader
                            // This is because optifine is a pain to work with and I don't have the motivation to support it
                            // since optifine is not open source and is a pain to work with
                        }
                    }
                } catch (e: Exception) {
                    Constants.LOGGER.error("Failed to update selected pack", e)
                }
                if (!file.delete()) {
                    gui.packsToDelete.add(file)
                } else if (gui.type == ProjectType.RESOURCE_PACK || gui.type == ProjectType.DATA_PACK) {
                    // We have to update the list
                    PaginatedScreen.backScreens.firstOrNull { it is PackSelectionScreen }?.let {
                        val model = (it as PackScreenAccessor).model
                        model.findNewPacks() // this will reload everything
                    }
                }
                gui.removeCard(this)
            }
            progressBox?.constraints?.width?.recalculate = true
        } else {
            cancel()
        }
    }

    fun cancel() {
        val updateUrl = selectedData?.version?.getDownloadUrl() ?: return
        gui.cancelUpdate(this)
        DownloadManager.cancelDownload(updateUrl)
        text?.setText("${ChatColor.BOLD}${localize("resourcify.updates.update")}")
    }

    fun getProgress(): Float {
        return DownloadManager.getProgress(selectedData?.version?.getDownloadUrl() ?: return 0f) ?: 0f
    }

    private fun getProject(): IProject {
        return selectedData?.project ?: data.values.first { it != null }!!.project
    }

    companion object {
        private val updateResourcePackLock = ReentrantLock()
    }
}