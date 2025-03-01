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

package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.elements.McImage
import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.gui.data.Icons
import dev.dediamondpro.resourcify.gui.projectpage.components.VersionCard
import dev.dediamondpro.resourcify.services.IVersion
import dev.dediamondpro.resourcify.util.*
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.UScreen

class VersionsPage(private val screen: ProjectScreen) : UIContainer() {
    private val versionsHolder = UIContainer().constrain {
        x = 0.pixels()
        y = 0.pixels()
        width = 100.percent()
        height = ChildBasedSizeConstraint()
    }.animateBeforeHide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, (-528).pixels())
    }.animateAfterUnhide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, 0.pixels())
    } childOf this
    private val changeLogHolder = UIBlock(Colors.BACKGROUND).constrain {
        x = 528.pixels()
        y = 0.pixels()
        width = 100.percent()
        height = ChildLocationSizeConstraint()
    }.animateBeforeHide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, 528.pixels())
    }.animateAfterUnhide {
        setXAnimation(Animations.IN_OUT_QUAD, 0.2f, 0.pixels())
    } childOf this

    init {
        constrain {
            x = 0.pixels(alignOpposite = true)
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint()
        }
        effect(ScissorEffect())
        screen.project.getVersions().thenApply { versions ->
            Window.enqueueRenderOperation {
                for (version in versions) {
                    VersionCard(
                        this, version, screen.service, screen.packHashes?.get(),
                        screen.downloadFolder, screen.type
                    ).constrain {
                        x = 0.pixels()
                        y = SiblingConstraint(padding = 4f)
                    } childOf versionsHolder
                }
            }
        }
        changeLogHolder.hide(true)
    }

    fun showChangelog(version: IVersion) {
        versionsHolder.hide()
        changeLogHolder.clearChildren()
        UIText("Versions ").constrain {
            x = 4.pixels()
            y = 8.pixels()
            color = Colors.TEXT_LINK.toConstraint()
        }.onMouseClick {
            changeLogHolder.hide()
            versionsHolder.unhide()
        } childOf changeLogHolder
        UIText("> ${version.getVersionNumber() ?: version.getName()}").constrain {
            x = SiblingConstraint()
            y = 8.pixels()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf changeLogHolder
        if (screen.packHashes != null && screen.downloadFolder != null) {
            VersionCard.createDownloadButton(version, screen.packHashes.get(), screen.downloadFolder, screen.type)
                ?.let {
                    it.constrain {
                        x = 4.pixels(true)
                        y = 4.pixels()
                    } childOf changeLogHolder
                }
        }
        version.getChangeLog().thenApply {
            Window.enqueueRenderOperation {
                var changelog = it
                if (version.hasDependencies()) changelog += "\n-----------"
                markdown(changelog, screen.service.getMarkdownStyle()).constrain {
                    x = 4.pixels()
                    y = SiblingConstraint(4f)
                    width = 100.percent() - 8.pixels()
                } childOf changeLogHolder
            }
            if (version.hasDependencies()) version.getDependencies().thenApply {
                Window.enqueueRenderOperation {
                    if (it.isNotEmpty()) UIText("resourcify.project.dependencies".localize()).constrain {
                        x = 4.pixels()
                        y = SiblingConstraint(padding = 4f)
                        color = Colors.TEXT_PRIMARY.toConstraint()
                    } childOf changeLogHolder
                    it.forEach { dependency ->
                        val project = dependency.project
                        val dependencyHolder = UIBlock(Colors.BACKGROUND).constrain {
                            x = 4.pixels()
                            y = SiblingConstraint(padding = 4f)
                            width = 100.percent() - 8.pixels()
                            height = 32.pixels()
                        }.onMouseClick {
                            val type = project.getType()
                            val downloadFolder = type.getDirectoryFromCurrent(screen.type, screen.downloadFolder)
                            UScreen.displayScreen(ProjectScreen(screen.service, project, type, downloadFolder))
                        } childOf changeLogHolder
                        val iconUrl = project.getIconUrl()
                        if (iconUrl == null) {
                            McImage(Icons.DEFAULT_ICON)
                        } else {
                            UIImage.ofURLCustom(
                                iconUrl,
                                width = 24f,
                                height = 24f,
                                fit = ImageURLUtils.Fit.COVER
                            )
                        }.constrain {
                            x = 4.pixels()
                            y = 4.pixels()
                            width = 24.pixels()
                            height = 24.pixels()
                        } childOf dependencyHolder
                        UIText(project.getName()).constrain {
                            x = 32.pixels()
                            y = 4.pixels()
                            color = Colors.TEXT_SECONDARY.toConstraint()
                        } childOf dependencyHolder
                        UIText(dependency.type.getLocalizedName()).constrain {
                            x = 32.pixels()
                            y = 4.pixels(alignOpposite = true)
                            color = Colors.TEXT_SECONDARY.toConstraint()
                        } childOf dependencyHolder
                    }
                }
            }
        }
        changeLogHolder.unhide()
    }
}