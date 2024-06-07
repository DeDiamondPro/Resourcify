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

import dev.dediamondpro.resourcify.gui.update.UpdateGui
import dev.dediamondpro.resourcify.gui.update.modrinth.FullModrinthProject
import dev.dediamondpro.resourcify.platform.Platform
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
    project: FullModrinthProject,
    private val newVersion: IVersion,
    val file: File,
    private val gui: UpdateGui
) : UIBlock(color = Color(0, 0, 0, 100)) {
    private val updateUrl = newVersion.getDownloadUrl().toURL()
    private var progressBox: UIBlock? = null
    private var text: UIText? = null

    init {
        constrain {
            height = 56.pixels()
        }

        if (project.iconUrl.isNullOrBlank()) {
            UIImage.ofResource("/assets/resourcify/pack.png")
        } else {
            UIImage.ofURL(project.iconUrl)
        }.constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = 48.pixels()
            height = 48.pixels()
        } childOf this
        UIText(project.title).constrain {
            x = 56.pixels()
            y = 8.pixels()
            textScale = 2.pixels()
        } childOf this
        UIText(newVersion.getName()).constrain {
            x = 56.pixels()
            y = SiblingConstraint(padding = 4f)
        } childOf this
        val versionNumberHolder = UIContainer().constrain {
            x = 56.pixels()
            y = SiblingConstraint(padding = 4f)
        } childOf this
        UIText(newVersion.getVersionType().localizedName.localize()).constrain {
            x = 0.pixels()
            y = 0.pixels()
            color = newVersion.getVersionType().color.toConstraint()
        } childOf versionNumberHolder
        newVersion.getVersionNumber()?.let {
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
            gui.showChangeLog(project, newVersion, createUpdateButton())
        } childOf buttonHolder
        UIText("${ChatColor.BOLD}${localize("resourcify.updates.changelog")}").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        } childOf changeLogButton
    }

    private fun createUpdateButton(): UIComponent {
        val updateButton = UIBlock(Color(27, 217, 106)).constrain {
            y = 0.pixels()
            width = 73.pixels()
            height = 50.percent() - 2.pixels()
        }.onMouseClick {
            downloadUpdate()
        }
        progressBox = UIBlock(Color(0, 0, 0, 100)).constrain {
            x = 0.pixels(true)
            y = 0.pixels()
            width = basicWidthConstraint {
                val progress = DownloadManager.getProgress(updateUrl)
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
        if (DownloadManager.getProgress(updateUrl) == null) {
            gui.registerUpdate(this, Platform.getSelectedResourcePacks().contains(file))
            text?.setText("${ChatColor.BOLD}${localize("resourcify.updates.updating")}")
            val newFileName = if (file.name == newVersion.getFileName()) {
                incrementFileName(newVersion.getFileName())
            } else {
                newVersion.getFileName()
            }
            val downloadFile = File(file.parentFile, newFileName)
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

    private fun incrementFileName(fileName: String): String {
        val regex = """\((\d+)\)(\.\w+)$""".toRegex()
        val matchResult = regex.find(fileName)

        return if (matchResult != null) {
            val currentNumber = matchResult.groupValues[1].toInt()
            val extension = matchResult.groupValues[2]
            fileName.replace(regex, "(${currentNumber + 1})$extension")
        } else {
            val dotIndex = fileName.lastIndexOf('.')
            if (dotIndex != -1) {
                fileName.substring(0, dotIndex) + " (1)." + fileName.substring(dotIndex + 1)
            } else {
                "$fileName (1)"
            }
        }
    }

    fun getProgress(): Float {
        return DownloadManager.getProgress(updateUrl) ?: 0f
    }

    companion object {
        private val updateResourcePackLock = ReentrantLock()
    }
}