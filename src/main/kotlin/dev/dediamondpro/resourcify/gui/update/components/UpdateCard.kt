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

package dev.dediamondpro.resourcify.gui.update.components

import dev.dediamondpro.resourcify.gui.update.UpdateGui
import dev.dediamondpro.resourcify.modrinth.ApiInfo
import dev.dediamondpro.resourcify.modrinth.ProjectResponse
import dev.dediamondpro.resourcify.modrinth.Version
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.util.DownloadManager
import dev.dediamondpro.resourcify.util.ofURL
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.ChatColor
import java.awt.Color
import java.io.File
import java.net.URL

//#if MC >= 11600
//$$ import dev.dediamondpro.resourcify.handlers.IrisHandler
//$$ import dev.dediamondpro.resourcify.gui.PaginatedScreen
//#endif

class UpdateCard(
    project: ProjectResponse,
    private val newVersion: Version,
    val file: File,
    private val gui: UpdateGui
) : UIBlock(color = Color(0, 0, 0, 100)) {
    private val newFile = newVersion.primaryFile!!
    private val updateUrl = URL(newFile.url)
    private var progressBox: UIBlock? = null
    private var text: UIText? = null

    init {
        constrain {
            height = 56.pixels()
        }

        if (project.iconUrl.isNullOrBlank()) {
            UIImage.ofResource("/pack.png")
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
        UIText(newVersion.name).constrain {
            x = 56.pixels()
            y = SiblingConstraint(padding = 4f)
        } childOf this
        val versionNumberHolder = UIContainer().constrain {
            x = 56.pixels()
            y = SiblingConstraint(padding = 4f)
        } childOf this
        UIText(newVersion.versionType.formattedName).constrain {
            x = 0.pixels()
            y = 0.pixels()
            color = newVersion.versionType.color.toConstraint()
        } childOf versionNumberHolder
        UIText(newVersion.versionNumber).constrain {
            x = SiblingConstraint(padding = 4f)
            y = 0.pixels()
        } childOf versionNumberHolder

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
        UIText("${ChatColor.BOLD}Changelog").constrain {
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
        text = UIText("${ChatColor.BOLD}Update").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        } childOf updateButton
        return updateButton
    }

    fun downloadUpdate() {
        if (DownloadManager.getProgress(updateUrl) == null) {
            gui.registerUpdate(this, Platform.getSelectedResourcePacks().contains(file))
            text?.setText("${ChatColor.BOLD}Updating...")
            val downloadFile = File(file.parentFile, newFile.fileName)
            DownloadManager.download(
                downloadFile,
                newFile.hashes.sha512, updateUrl
            ) {
                when (gui.type) {
                    ApiInfo.ProjectType.RESOURCE_PACK -> {
                        if (Platform.getSelectedResourcePacks().contains(file)) Window.enqueueRenderOperation {
                            Platform.replaceResourcePack(file, downloadFile)
                        } else {
                            Platform.closeResourcePack(file)
                        }
                    }

                    //#if MC >= 11600
                    //$$ ApiInfo.ProjectType.IRIS_SHADER -> {
                    //$$     PaginatedScreen.backScreens.firstOrNull { it !is PaginatedScreen }?.let {
                    //$$         if (IrisHandler.getActiveShader(it) == file.name) {
                    //$$             IrisHandler.applyShaders(it, downloadFile.name)
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
                if (!file.delete()) gui.packsToDelete.add(file)
                gui.removeCard(this)
            }
            progressBox?.constraints?.width?.recalculate = true
        } else {
            gui.cancelUpdate(this)
            DownloadManager.cancelDownload(updateUrl)
            text?.setText("${ChatColor.BOLD}Update")
        }
    }

    fun getProgress(): Float {
        return DownloadManager.getProgress(updateUrl) ?: 0f
    }
}