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

package dev.dediamondpro.resourcify.gui.projectpage.components

import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.gui.projectpage.VersionWrapper
import dev.dediamondpro.resourcify.gui.projectpage.VersionsPage
import dev.dediamondpro.resourcify.services.IService
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.util.*
import gg.essential.elementa.UIComponent
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
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class VersionCard(
    parent: VersionsPage, val version: IVersion, val service: IService,
    hashes: List<String>?, downloadFolder: File?, val type: ProjectType
) : UIBlock(Colors.BACKGROUND) {

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
        UIText(version.getName().trim()).constrain {
            x = 6.pixels()
            y = 0.pixels()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf versionInfo
        val infoHolder = UIContainer().constrain {
            x = 6.pixels()
            y = SiblingConstraint(padding = 2f)
            width = 45.percent() - 12.pixels()
            height = ChildBasedMaxSizeConstraint()
        } childOf versionInfo
        UIText(version.getVersionType().localizedName).constrain {
            x = 0.pixels()
            y = 0.pixels()
            color = version.getVersionType().color.toConstraint()
        } childOf infoHolder
        version.getVersionNumber()?.let {
            UIText(it).constrain {
                x = SiblingConstraint(padding = 4f)
                y = 0.pixels()
                color = Colors.TEXT_SECONDARY.toConstraint()
            } childOf infoHolder
        }

        val mcVersionContainer = UIContainer().constrain {
            x = 45.percent - 2.pixels()
            y = 6.pixels()
            width = 15.percent() - 4.pixels()
            height = ChildBasedSizeConstraint(padding = 2f)
        } childOf this
        UIWrappedText(version.getLoaders().joinToString(", ") { it.capitalizeAll() }).constrain {
            y = 0.pixels()
            width = 100.percent()
            color = Colors.TEXT_SECONDARY.toConstraint()
        } childOf mcVersionContainer
        UIWrappedText(getFormattedVersions()).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 2f)
            width = 100.percent()
            color = Colors.TEXT_SECONDARY.toConstraint()
        } childOf mcVersionContainer

        val statsContainer = UIContainer().constrain {
            x = 60.percent() - 2.pixels()
            y = 6.pixels()
            width = 40.percent() - 81.pixels()
            height = ChildBasedSizeConstraint(padding = 2f)
        } effect ScissorEffect() childOf this
        UIText("resourcify.version.download_count".localize(version.getDownloadCount())).constrain {
            y = 0.pixels()
            color = Colors.TEXT_SECONDARY.toConstraint()
        } childOf statsContainer
        val instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(version.getReleaseDate()))
        UIText("resourcify.version.published_on".localize(Date.from(instant))).constrain {
            y = SiblingConstraint(padding = 2f)
            color = Colors.TEXT_SECONDARY.toConstraint()
        } childOf statsContainer

        if (hashes != null && downloadFolder != null) {
            val versionWrapped = VersionWrapper(version, type, hashes.contains(version.getSha1()))
            val button = createDownloadButton(versionWrapped, downloadFolder, parent.screen)
            if (button != null) {
                button childOf this
                onMouseClick {
                    if (button.isPointInside(it.absoluteX, it.absoluteY)) return@onMouseClick
                    parent.showChangelog(versionWrapped)
                }
            }
        }
    }

    private fun getFormattedVersions(): String {
        val versions = version.getMinecraftVersions()
        if (versions.isEmpty()) {
            return ""
        }
        val allVersionsFuture = service.getMinecraftVersions()
        if (!allVersionsFuture.isDone || allVersionsFuture.isCompletedExceptionally || allVersionsFuture.isCancelled) {
            return ""
        }
        val allVersions = allVersionsFuture.getNow(emptyMap())?.values?.reversed()
        if (allVersions.isNullOrEmpty()) {
            return versions.joinToString(", ")
        }
        return buildString {
            var currentVersionIndex = 0
            while (currentVersionIndex < versions.size) {
                var allVersionIndex = allVersions.indexOf(versions[currentVersionIndex])
                if (allVersionIndex == -1) {
                    currentVersionIndex += 1
                    continue
                }
                val versionGroup = mutableListOf(versions[currentVersionIndex])
                while (
                    currentVersionIndex + 1 < versions.size
                    && allVersions.indexOf(versions[currentVersionIndex + 1]) == -1
                ) {
                    currentVersionIndex += 1
                }
                while (
                    currentVersionIndex + 1 < versions.size
                    && allVersionIndex + 1 < allVersions.size
                    && versions[currentVersionIndex + 1] == allVersions[allVersionIndex + 1]
                ) {
                    currentVersionIndex += 1
                    allVersionIndex += 1
                    versionGroup.add(versions[currentVersionIndex])
                    while (
                        currentVersionIndex + 1 < versions.size
                        && allVersions.indexOf(versions[currentVersionIndex + 1]) == -1
                    ) {
                        currentVersionIndex += 1
                    }
                }
                append(
                    if (versionGroup.size > 1) "${versionGroup.first()}—${versionGroup.last()}"
                    else versionGroup.first()
                )
                append(", ")
                currentVersionIndex += 1
            }
        }.removeSuffix(", ")
    }

    companion object {
        fun createDownloadButton(version: VersionWrapper, downloadFolder: File, parent: PaginatedScreen): UIComponent? {
            val buttonText =
                "${ChatColor.BOLD}${if (version.installed) "resourcify.version.installed".localize() else "resourcify.version.install".localize()}"
            var text: UIText? = null
            val downloadButton = UIBlock(Colors.BUTTON_PRIMARY).constrain {
                x = 6.pixels(true)
                y = CenterConstraint()
                width = 73.pixels()
                height = 18.pixels()
            }.onMouseClick {
                if (it.mouseButton != 0) return@onMouseClick
                version.download(downloadFolder, text, parent)
            }
            version.get().getDownloadUrl()?.let { url ->
                UIBlock(Color(0, 0, 0, 100)).constrain {
                    x = 0.pixels(true)
                    y = 0.pixels()
                    width = basicWidthConstraint {
                        val progress = DownloadManager.getProgress(url)
                        if (progress == null) 0f
                        else (1 - progress) * it.parent.getWidth()
                    }
                    height = 18.pixels()
                } childOf downloadButton
            }
            text = UIText(buttonText).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                color = Colors.TEXT_PRIMARY.toConstraint()
            } childOf downloadButton
            return downloadButton
        }
    }
}