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

package dev.dediamondpro.resourcify.gui.projectpage.components

import dev.dediamondpro.resourcify.modrinth.GameVersions
import dev.dediamondpro.resourcify.modrinth.Version
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.util.DownloadManager
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.ChatColor
import java.awt.Color
import java.io.File
import java.net.URL
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class VersionCard(val version: Version, val hashes: List<String>) : UIBlock(color = Color(0, 0, 0, 100)) {
    private val df = SimpleDateFormat("MMM d, yyyy")
    private val nf = NumberFormat.getInstance()

    init {
        constrain {
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint() + 8.pixels()
        }
        val versionInfo = UIContainer().constrain {
            y = 6.pixels()
            width = 45.percent() - 12.pixels()
            height = ChildBasedSizeConstraint(padding = 2f)
        } effect ScissorEffect() childOf this
        UIText(version.name.trim()).constrain {
            x = 6.pixels()
            y = 0.pixels()
        } childOf versionInfo
        val infoHolder = UIContainer().constrain {
            x = 6.pixels()
            y = SiblingConstraint(padding = 2f)
            width = 45.percent() - 12.pixels()
            height = ChildBasedMaxSizeConstraint()
        } childOf versionInfo
        UIText(version.versionType.formattedName).constrain {
            x = 0.pixels()
            y = 0.pixels()
            color = version.versionType.color.toConstraint()
        } childOf infoHolder
        UIText(version.versionNumber).constrain {
            x = SiblingConstraint(padding = 4f)
            y = 0.pixels()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf infoHolder

        val mcVersionContainer = UIContainer().constrain {
            x = 45.percent - 2.pixels()
            y = 6.pixels()
            width = 15.percent() - 4.pixels()
            height = ChildBasedSizeConstraint(padding = 2f)
        } childOf this
        UIText("Minecraft").constrain {
            y = 0.pixels()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf mcVersionContainer
        UIWrappedText(GameVersions.formatVersions(version.gameVersions)).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 2f)
            width = 100.percent()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf mcVersionContainer

        val statsContainer = UIContainer().constrain {
            x = 60.percent() - 2.pixels()
            y = 6.pixels()
            width = 40.percent() - 81.pixels()
            height = ChildBasedSizeConstraint(padding = 2f)
        } effect ScissorEffect() childOf this
        UIText("${nf.format(version.downloads)} downloads").constrain {
            y = 0.pixels()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf statsContainer
        val instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(version.datePublished))
        UIText("Published on ${df.format(Date.from(instant))}").constrain {
            y = SiblingConstraint(padding = 2f)
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf statsContainer

        downloadButton()
    }

    private fun downloadButton() {
        val fileToDownload = version.files.firstOrNull {
            it.primary
        } ?: version.files.firstOrNull() ?: return
        val url = URL(fileToDownload.url)
        var installed = hashes.contains(fileToDownload.hashes.sha1)
        val buttonText = if (installed) "${ChatColor.BOLD}Installed" else "${ChatColor.BOLD}Install"

        var progressBox: UIBlock? = null
        var text: UIText? = null
        val downloadButton = UIBlock(Color(27, 217, 106)).constrain {
            x = 6.pixels(true)
            y = CenterConstraint()
            width = 73.pixels()
            height = 18.pixels()
        }.onMouseClick {
            if (installed || it.mouseButton != 0) return@onMouseClick
            if (DownloadManager.getProgress(url) == null) {
                text?.setText("${ChatColor.BOLD}Installing...")
                DownloadManager.download(
                    File(Platform.getResourcePackDirectory(), fileToDownload.fileName), url
                ) {
                    text?.setText("${ChatColor.BOLD}Installed")
                    installed = true
                }
                progressBox?.constraints?.width?.recalculate = true
            } else {
                DownloadManager.cancelDownload(url)
                text?.setText("${ChatColor.BOLD}Install")
            }
        } childOf this
        progressBox = UIBlock(Color(0, 0, 0, 100)).constrain {
            x = 0.pixels(true)
            y = 0.pixels()
            width = basicWidthConstraint {
                val progress = DownloadManager.getProgress(url)
                if (progress == null) 0f
                else (1 - progress) * it.parent.getWidth()
            }
            height = 18.pixels()
        } childOf downloadButton
        text = UIText(buttonText).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        } childOf downloadButton
    }
}