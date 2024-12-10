/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.elements.DropDown
import dev.dediamondpro.resourcify.gui.update.UpdateGui
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.services.IProject
import dev.dediamondpro.resourcify.services.IService
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.util.*
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.ChatColor
import java.awt.Color
import java.io.File
import java.util.concurrent.locks.ReentrantLock

//#if MC >= 11600
//$$ import dev.dediamondpro.resourcify.handlers.IrisHandler
//$$ import dev.dediamondpro.resourcify.gui.PaginatedScreen
//$$ import gg.essential.elementa.components.Window
//#endif

class UpdateCard(
    private val data: Map<IService, UpdateGui.UpdateData?>,
    val file: File,
    private val gui: UpdateGui
) : UIBlock(color = Color(0, 0, 0, 100)) {
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
        val iconUrl = project.getIconUrl()
        if (iconUrl == null) {
            UIImage.ofResourceCustom("/assets/resourcify/pack.png")
        } else {
            UIImage.ofURLCustom(iconUrl)
        }.constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = 48.pixels()
            height = 48.pixels()
        } childOf this
        UIText(project.getName()).constrain {
            x = 56.pixels()
            y = 8.pixels()
            textScale = 2.pixels()
        } childOf this

        if (version == null) {
            UIText("${ChatColor.YELLOW}${"resourcify.updates.up-to-date".localize()}").constrain {
                x = 56.pixels()
                y = SiblingConstraint(padding = 4f)
            } childOf this
        } else {
            UIText(version.getName()).constrain {
                x = 56.pixels()
                y = SiblingConstraint(padding = 4f)
            } childOf this
            val versionNumberHolder = UIContainer().constrain {
                x = 56.pixels()
                y = SiblingConstraint(padding = 4f)
            } childOf this
            UIText(version.getVersionType().localizedName.localize()).constrain {
                x = 0.pixels()
                y = 0.pixels()
                color = version.getVersionType().color.toConstraint()
            } childOf versionNumberHolder
            version.getVersionNumber()?.let {
                UIText(it).constrain {
                    x = SiblingConstraint(padding = 4f)
                    y = 0.pixels()
                } childOf versionNumberHolder
            }

            val buttonHolder = UIContainer().constrain {
                x = 4.pixels(true)
                y = 4.pixels()
                width = 73.pixels()
                height = 48.pixels()
            } childOf this

            createUpdateButton() childOf buttonHolder

            val changeLogButton = UIBlock(Color(150, 150, 150)).constrain {
                y = 0.pixels(true)
                width = 73.pixels()
                height = 50.percent() - 2.pixels()
            }.onMouseClick {
                gui.showChangeLog(project, version, createUpdateButton())
            } childOf buttonHolder
            UIText("${ChatColor.BOLD}${localize("resourcify.updates.changelog")}").constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            } childOf changeLogButton
        }

        val sourceHolder = UIContainer().constrain {
            x = 50.percent() - 50.pixels()
            y = 4.pixels()
            width = 100.pixels()
            height = 48.pixels()
        } childOf this
        val sourceTextHolder = UIContainer().constrain {
            height = 50.percent()
        } childOf sourceHolder
        UIText("resourcify.updates.source".localize()).constrain {
            y = CenterConstraint()
        } childOf sourceTextHolder

        DropDown(
            data.keys.map { it.getName() }, onlyOneOption = true,
            selectedOptions = mutableListOf(selectedService.getName())
        ).onSelectionUpdate { newService ->
            selectedService = data.keys.firstOrNull { it.getName() == newService.first() } ?: return@onSelectionUpdate
            selectedData = data[selectedService]

            this@UpdateCard.clearChildren()
            createCard(getProject(), selectedData?.version)
        }.constrain {
            x = 0.pixels()
            y = 0.pixels(true)
            width = 100.percent()
            height = 50.percent() - 2.pixels()
        } childOf sourceHolder
    }

    private fun createUpdateButton(): UIComponent {
        val updateButton = UIBlock(Color(27, 217, 106)).constrain {
            y = 0.pixels()
            width = 73.pixels()
            height = 50.percent() - 2.pixels()
        }.onMouseClick {
            downloadUpdate()
        }
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
        text = UIText("${ChatColor.BOLD}${localize("resourcify.updates.update")}").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        } childOf updateButton
        return updateButton
    }

    fun downloadUpdate() {
        val updateUrl = selectedData?.version?.getDownloadUrl() ?: return
        val newVersion = selectedData?.version ?: return
        if (DownloadManager.getProgress(updateUrl) == null) {
            gui.registerUpdate(this, Platform.getSelectedResourcePacks().contains(file))
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
                        ProjectType.AYCY_RESOURCE_PACK, ProjectType.RESOURCE_PACK -> {
                            try {
                                // If multiple threads try to update stuff at the same time things can go very wrong
                                updateResourcePackLock.lock()
                                val position = Platform.closeResourcePack(file)
                                if (position != -1) {
                                    Platform.enableResourcePack(downloadFile, position)
                                }
                                Platform.saveSettings()
                            } finally {
                                updateResourcePackLock.unlock()
                            }
                        }

                        //#if MC >= 11600
                        //$$ ProjectType.IRIS_SHADER -> {
                        //$$     PaginatedScreen.backScreens.firstOrNull { it !is PaginatedScreen }?.let {
                        //$$         if (IrisHandler.getActiveShader(it) == file.name) {
                        //$$             Window.enqueueRenderOperation{ IrisHandler.applyShaders(it, downloadFile.name) }
                        //$$         }
                        //$$     }
                        //$$ }
                        //#endif

                        else -> {
                            // Other types (Optifine shaders) don't have an implementation to update the selected shader
                            // This is because optifine is a pain to work with and I don't have the motivation to support it
                            // since optifine is not open source and is a pain to work with
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (!file.delete()) gui.packsToDelete.add(file)
                gui.removeCard(this)
            }
            progressBox?.constraints?.width?.recalculate = true
        } else {
            gui.cancelUpdate(this)
            DownloadManager.cancelDownload(updateUrl)
            text?.setText("${ChatColor.BOLD}${localize("resourcify.updates.update")}")
        }
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