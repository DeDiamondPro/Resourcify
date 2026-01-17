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

import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.elementa.style.MarkdownTextStyle
import dev.dediamondpro.minemark.style.LinkStyleConfig
import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.constraints.CustomImageAspectConstraint
import dev.dediamondpro.resourcify.elements.McImage
import dev.dediamondpro.resourcify.elements.Paginator
import dev.dediamondpro.resourcify.elements.TextIcon
import dev.dediamondpro.resourcify.gui.ConfirmLinkScreen
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.gui.data.Icons
import dev.dediamondpro.resourcify.gui.projectpage.components.MemberCard
import dev.dediamondpro.resourcify.platform.Platform
import dev.dediamondpro.resourcify.services.IProject
import dev.dediamondpro.resourcify.services.IService
import dev.dediamondpro.resourcify.services.ProjectType
import dev.dediamondpro.resourcify.util.*
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.font.DefaultFonts
import gg.essential.universal.ChatColor
import gg.essential.universal.UDesktop
import java.awt.Color
import java.io.File
import java.net.URI
import java.util.concurrent.CompletableFuture
import kotlin.math.min

class ProjectScreen(
    val service: IService,
    val project: IProject,
    val type: ProjectType,
    val downloadFolder: File?
) : PaginatedScreen() {
    val packHashes: CompletableFuture<List<String>>? = downloadFolder?.let {
        supplyAsync { PackUtils.getPackHashes(it) }
    }

    private val scrollBox = ScrollComponent(pixelsPerScroll = 30f, scrollAcceleration = 1.5f).constrain {
        width = 100.percent()
        height = 100.percent()
    } childOf window

    private val contentBox = UIContainer().constrain {
        x = CenterConstraint()
        y = 4.pixels()
        width = MinConstraint(ChildBasedSizeConstraint(padding = 4f), basicWidthConstraint {
            window.getWidth() - 8
        })
        height = ChildLocationSizeConstraint() + 4.pixels()
    } childOf scrollBox

    private val sideContainer = UIContainer().constrain {
        x = 0.pixels()
        y = 0.pixels()
        width = 160.pixels()
        height = ChildBasedSizeConstraint()
    } childOf contentBox

    private val mainBox = UIContainer().constrain {
        x = 0.pixels(alignOpposite = true)
        y = 0.pixels()
        width = basicWidthConstraint { min(528f, window.getWidth() - 172) }
        height = ChildLocationSizeConstraint()
    } childOf contentBox

    init {
        mainBody()
        sideBar()
    }

    private fun mainBody() {
        // top navbar
        val navigationBox = UIBlock(Colors.BACKGROUND).constrain {
            x = 0.pixels(alignOpposite = true)
            y = SiblingConstraint()
            width = 100.percent()
            height = 29.pixels()
        } childOf mainBox

        project.getVersions().thenAccept { versions ->
            val version = versions?.firstOrNull {
                it.getMinecraftVersions().contains(Platform.getMcVersion())
            } ?: return@thenAccept

            if (downloadFolder == null) {
                Window.enqueueRenderOperation {
                    val text = UIText("${ChatColor.BOLD}${localize("resourcify.version.install")}").constrain {
                        x = CenterConstraint()
                        y = CenterConstraint()
                        color = Colors.TEXT_PRIMARY.toConstraint()
                    }
                    val downloadButton = UIBlock(Colors.BUTTON_PRIMARY).constrain {
                        x = 6.pixels(true)
                        y = CenterConstraint()
                        width = basicWidthConstraint { text.getWidth() + 8f }
                        height = 18.pixels()
                    }.onMouseClick {
                        displayScreen(ConfirmLinkScreen(version.getViewUrl().toString(), this@ProjectScreen, true))
                    } childOf navigationBox
                    text childOf downloadButton
                }
                return@thenAccept
            }

            val installed = packHashes!!.get().contains(version.getSha1())

            val installText = version.getVersionNumber()?.let {
                "${ChatColor.BOLD}${localize("resourcify.version.install_version", it)}"
            } ?: "${ChatColor.BOLD}${localize("resourcify.version.install")}"
            val buttonText = if (installed) {
                "${ChatColor.BOLD}${localize("resourcify.version.installed")}"
            } else {
                installText
            }

            val versionWrapped = VersionWrapper(version, type, installed)
            Window.enqueueRenderOperation {
                var text: UIText? = null
                val downloadButton = UIBlock(Colors.BUTTON_PRIMARY).constrain {
                    x = 6.pixels(true)
                    y = CenterConstraint()
                    width = basicWidthConstraint { (text?.getWidth() ?: 0f) + 8f }
                    height = 18.pixels()
                }.onMouseClick {
                    if (it.mouseButton != 0) return@onMouseClick
                    versionWrapped.download(downloadFolder, text, this@ProjectScreen, installText)
                } childOf navigationBox
                UIBlock(Color(0, 0, 0, 100)).constrain {
                    x = 0.pixels(true)
                    y = 0.pixels()
                    width = basicWidthConstraint {
                        val progress = version.getDownloadUrl()?.let { DownloadManager.getProgress(it) }
                        if (progress == null) 0f
                        else (1 - progress) * it.parent.getWidth()
                    }
                    height = 18.pixels()
                } childOf downloadButton
                text = UIText(buttonText).constrain {
                    x = CenterConstraint()
                    y = CenterConstraint()
                    color = Colors.TEXT_PRIMARY.toConstraint()
                } childOf downloadButton
            }
        }

        var currentPage: (ProjectScreen) -> UIComponent = ::DescriptionPage
        val loadedPages = mutableMapOf<(ProjectScreen) -> UIComponent, UIComponent>()
        loadedPages[::DescriptionPage] = DescriptionPage(this) childOf mainBox
        val pages = mutableMapOf<String, (ProjectScreen) -> UIComponent>()
        pages["resourcify.project.description".localize()] = ::DescriptionPage
        if (project.hasGallery()) pages["resourcify.project.gallery".localize()] = ::GalleryPage
        pages["resourcify.project.versions".localize()] = ::VersionsPage
        pages.forEach { (text, page) ->
            val hitBox = UIContainer().constrain {
                x = if (text == "resourcify.project.description".localize()) 6.pixels()
                else SiblingConstraint(padding = 8f)
                y = CenterConstraint()
                width = ChildBasedSizeConstraint()
                height = 19.pixels()
            }.onMouseClick {
                if (page == currentPage || it.mouseButton != 0) return@onMouseClick
                currentPage = page
                loadedPages.forEach { (_, page) -> page.hide() }
                loadedPages.getOrPut(page) {
                    page(this@ProjectScreen) childOf mainBox
                }.unhide()
            } childOf navigationBox

            UIText("${ChatColor.BOLD}$text").constrain {
                y = CenterConstraint()
                color = Colors.TEXT_PRIMARY.toConstraint()
            } childOf hitBox
        }
        val sourceHitbox = UIContainer().constrain {
            x = SiblingConstraint(padding = 8f)
            y = CenterConstraint()
            width = ChildBasedSizeConstraint()
            height = 19.pixels()
        }.onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            UDesktop.browse(URI(project.getBrowserUrl()))
        } childOf navigationBox
        TextIcon(
            "${ChatColor.BOLD}${service.getName().localize()}",
            Icons.EXTERNAL_LINK,
            color = Colors.TEXT_PRIMARY.toConstraint()
        ).constrain {
            y = CenterConstraint()
            width = ChildLocationSizeConstraint()
            height = ChildBasedMaxSizeConstraint()
        } childOf sourceHitbox
    }

    private fun sideBar() {
        Paginator(this).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 160.pixels()
            height = 29.pixels()
        } childOf sideContainer

        val sideBox = UIBlock(Colors.BACKGROUND).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 160.pixels()
            height = ChildBasedSizeConstraint() + 4.pixels()
        } childOf sideContainer
        val bannerUrl = project.getBannerUrl()
        if (bannerUrl != null) UIImage.ofURLCustom(bannerUrl, false).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = CustomImageAspectConstraint()
        } childOf sideBox
        val iconUrl = project.getIconUrl()
        (if (iconUrl == null) McImage(Icons.DEFAULT_ICON)
        else UIImage.ofURLCustom(iconUrl))
            .constrain {
                x = 4.pixels()
                y = SiblingConstraint(padding = 4f)
                width = 64.pixels()
                height = 64.pixels()
            } childOf sideBox
        UIWrappedText(project.getName()).constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 152.pixels()
            textScale = 1.5.pixels()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf sideBox
        UIWrappedText(project.getSummary()).constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 152.pixels()
            color = Colors.TEXT_SECONDARY.toConstraint()
        } childOf sideBox

        val categoryHolder = UIContainer().constrain {
            x = 4.pixels()
            y = SiblingConstraint()
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        } childOf sideBox
        project.getCategories().thenAccept {
            if (it.isEmpty()) return@thenAccept
            Window.enqueueRenderOperation {
                categoryHolder.constrain { y = SiblingConstraint(padding = 8f) }
                UIText("resourcify.project.categories".localize()).constrain {
                    x = 0.pixels()
                    y = 0.pixels()
                    color = Colors.TEXT_SECONDARY.toConstraint()
                } childOf categoryHolder
                it.forEach { category ->
                    UIText("- ${category.capitalizeAll()}").constrain {
                        x = 0.pixels()
                        y = SiblingConstraint(padding = 2f)
                        color = Colors.TEXT_SECONDARY.toConstraint()
                    } childOf categoryHolder
                }
            }
        }
        val externalResourcesBox = UIContainer().constrain {
            x = 0.pixels()
            y = SiblingConstraint()
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        } childOf sideBox
        project.getExternalLinks().thenAccept {
            if (it.isEmpty()) return@thenAccept
            Window.enqueueRenderOperation {
                externalResourcesBox.constrain { y = SiblingConstraint(padding = 8f) }
                UIText("resourcify.project.external_resources".localize()).constrain {
                    x = 4.pixels()
                    y = 0.pixels()
                    color = Colors.TEXT_SECONDARY.toConstraint()
                } childOf externalResourcesBox
                markdown(
                    it.map { (name, url) -> "[$name]($url)" }.joinToString(" ● "),
                    style = MarkdownStyle(
                        textStyle = MarkdownTextStyle(
                            1f, Colors.TEXT_SECONDARY, 1f, DefaultFonts.VANILLA_FONT_RENDERER
                        ),
                        linkStyle = LinkStyleConfig(Colors.TEXT_SECONDARY, ConfirmingBrowserProvider)
                    ),
                ).constrain {
                    x = 4.pixels()
                    y = SiblingConstraint(padding = 2f)
                    width = 100.percent() - 8.pixels()
                } childOf externalResourcesBox
            }
        }
        val membersBox = UIContainer().constrain {
            x = 0.pixels()
            y = SiblingConstraint()
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        } childOf sideBox
        project.getMembers().thenAccept {
            if (it.isNullOrEmpty()) return@thenAccept
            Window.enqueueRenderOperation {
                membersBox.constrain { y = SiblingConstraint(padding = 8f) }
                UIText("resourcify.project.members".localize()).constrain {
                    x = 4.pixels()
                    y = 0.pixels()
                    color = Colors.TEXT_SECONDARY.toConstraint()
                } childOf membersBox
                it.forEach { member ->
                    MemberCard(member).constrain {
                        x = 4.pixels()
                        y = SiblingConstraint(padding = 2f)
                        width = 100.percent() - 8.pixels()
                    } childOf membersBox
                }
            }
        }
    }
}