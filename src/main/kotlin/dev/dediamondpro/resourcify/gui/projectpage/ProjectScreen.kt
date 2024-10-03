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

package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.elementa.style.MarkdownTextStyle
import dev.dediamondpro.minemark.style.LinkStyleConfig
import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.constraints.WindowMinConstraint
import dev.dediamondpro.resourcify.elements.Paginator
import dev.dediamondpro.resourcify.elements.TextIcon
import dev.dediamondpro.resourcify.gui.ConfirmLinkScreen
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.projectpage.components.MemberCard
import dev.dediamondpro.resourcify.mixins.WorldSelectionScreenAccessor
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
import net.minecraft.client.gui.GuiSelectWorld
import java.awt.Color
import java.io.File
import java.net.URI
import java.util.concurrent.CompletableFuture

class ProjectScreen(
    val service: IService,
    val project: IProject,
    val type: ProjectType,
    val downloadFolder: File
) : PaginatedScreen() {
    val packHashes: CompletableFuture<List<String>> = supplyAsync {
        PackUtils.getPackHashes(downloadFolder)
    }

    private val scrollBox = ScrollComponent(pixelsPerScroll = 30f, scrollAcceleration = 1.5f).constrain {
        width = 100.percent()
        height = 100.percent()
    } childOf window

    private val contentBox = UIContainer().constrain {
        x = CenterConstraint()
        y = 4.pixels()
        width = ChildBasedSizeConstraint(padding = 4f)
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
        width = WindowMinConstraint(528.pixels())
        height = ChildLocationSizeConstraint()
    } childOf contentBox

    init {
        mainBody()
        sideBar()
    }

    private fun mainBody() {
        // top navbar
        val navigationBox = UIBlock(color = Color(0, 0, 0, 100)).constrain {
            x = 0.pixels(alignOpposite = true)
            y = SiblingConstraint()
            width = 100.percent()
            height = 29.pixels()
        } childOf mainBox

        // If the project can not be installed by resourcify (because, for example, redistribution is disabled on cf)
        // a link to the website will be displayed
        if (!project.canBeInstalled()) {
            val text = UIText("${ChatColor.BOLD}${localize("resourcify.version.install")}").constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            }
            val downloadButton = UIBlock(Color(27, 217, 106)).constrain {
                x = 6.pixels(true)
                y = CenterConstraint()
                width = basicWidthConstraint { text.getWidth() + 8f }
                height = 18.pixels()
            }.onMouseClick {
                displayScreen(ConfirmLinkScreen(project.getBrowserUrl(), this@ProjectScreen, true))
            } childOf navigationBox
            text childOf downloadButton
        } else project.getVersions().thenAccept { versions ->
            if (versions == null) return@thenAccept
            val version = versions.firstOrNull {
                it.getMinecraftVersions().contains(Platform.getMcVersion())
            } ?: return@thenAccept
            val url = version.getDownloadUrl().toURL()
            var installed = packHashes.get().contains(version.getSha1())
            val buttonText = if (installed) "${ChatColor.BOLD}${localize("resourcify.version.installed")}"
            else version.getVersionNumber()?.let {
                "${ChatColor.BOLD}${localize("resourcify.version.install_version", it)}"
            } ?: "${ChatColor.BOLD}${localize("resourcify.version.install")}"
            Window.enqueueRenderOperation {
                var progressBox: UIBlock? = null
                var text: UIText? = null
                val downloadButton = UIBlock(Color(27, 217, 106)).constrain {
                    x = 6.pixels(true)
                    y = CenterConstraint()
                    width = basicWidthConstraint { (text?.getWidth() ?: 0f) + 8f }
                    height = 18.pixels()
                }.onMouseClick {
                    if (installed || it.mouseButton != 0) return@onMouseClick
                    if (DownloadManager.getProgress(url) == null) {
                        text?.setText("${ChatColor.BOLD}${localize("resourcify.version.installing")}")
                        var fileName = version.getFileName()
                        if (type.shouldExtract) {
                            fileName = fileName.removeSuffix(".zip")
                        }
                        var file = File(downloadFolder, fileName)
                        if (file.exists()) {
                            file = File(downloadFolder, Utils.incrementFileName(version.getFileName()))
                        }
                        DownloadManager.download(file, version.getSha1(), url, type.shouldExtract) {
                            text?.setText("${ChatColor.BOLD}${localize("resourcify.version.installed")}")
                            installed = true
                        }
                        progressBox?.constraints?.width?.recalculate = true
                    } else {
                        DownloadManager.cancelDownload(url)
                        text?.setText(
                            version.getVersionNumber()?.let { versionNumber ->
                                "${ChatColor.BOLD}${localize("resourcify.version.install_version", versionNumber)}"
                            } ?: "${ChatColor.BOLD}${localize("resourcify.version.install")}"
                        )
                    }
                } childOf navigationBox
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

        var currentPage: (ProjectScreen) -> UIComponent = ::DescriptionPage
        val loadedPages = mutableMapOf<(ProjectScreen) -> UIComponent, UIComponent>()
        loadedPages[::DescriptionPage] = DescriptionPage(this) childOf mainBox
        val pages = mutableMapOf<String, (ProjectScreen) -> UIComponent>()
        pages["resourcify.project.description".localize()] = ::DescriptionPage
        if (project.hasGallery()) pages["resourcify.project.gallery".localize()] = ::GalleryPage
        if (project.canBeInstalled()) pages["resourcify.project.versions".localize()] = ::VersionsPage
        pages.forEach { (text, page) ->
            UIText("${ChatColor.BOLD}$text").constrain {
                x = if (text == "resourcify.project.description".localize()) 6.pixels()
                else SiblingConstraint(padding = 8f)
                y = CenterConstraint()
            }.onMouseClick {
                if (page == currentPage || it.mouseButton != 0) return@onMouseClick
                currentPage = page
                loadedPages.forEach { (_, page) -> page.hide() }
                loadedPages.getOrPut(page) {
                    page(this@ProjectScreen) childOf mainBox
                }.unhide()
            } childOf navigationBox
        }
        TextIcon("${ChatColor.BOLD}${service.getName().localize()}", Icons.EXTERNAL_LINK).constrain {
            x = SiblingConstraint(padding = 8f)
            y = CenterConstraint()
            width = ChildLocationSizeConstraint()
            height = ChildBasedMaxSizeConstraint()
        }.onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            UDesktop.browse(URI(project.getBrowserUrl()))
        } childOf navigationBox
    }

    private fun sideBar() {
        Paginator(this).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 160.pixels()
            height = 29.pixels()
        } childOf sideContainer

        val sideBox = UIBlock(color = Color(0, 0, 0, 100)).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 160.pixels()
            height = ChildBasedSizeConstraint() + 4.pixels()
        } childOf sideContainer
        val bannerUrl = project.getBannerUrl()
        if (bannerUrl != null) UIImage.ofURL(bannerUrl, false).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = ImageAspectConstraint()
        } childOf sideBox
        val iconUrl = project.getIconUrl()
        (if (iconUrl.isNullOrBlank()) UIImage.ofResource("/assets/resourcify/pack.png")
        else UIImage.ofURL(iconUrl))
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
        } childOf sideBox
        UIWrappedText(project.getSummary()).constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 152.pixels()
            color = Color.LIGHT_GRAY.toConstraint()
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
                    color = Color.LIGHT_GRAY.toConstraint()
                } childOf categoryHolder
                it.forEach { category ->
                    UIText("- ${category.capitalizeAll()}").constrain {
                        x = 0.pixels()
                        y = SiblingConstraint(padding = 2f)
                        color = Color.LIGHT_GRAY.toConstraint()
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
                    color = Color.LIGHT_GRAY.toConstraint()
                } childOf externalResourcesBox
                markdown(
                    it.map { (name, url) -> "[$name]($url)" }.joinToString(" ● "),
                    style = MarkdownStyle(
                        textStyle = MarkdownTextStyle(
                            1f, Color.LIGHT_GRAY, 1f, DefaultFonts.VANILLA_FONT_RENDERER
                        ),
                        linkStyle = LinkStyleConfig(Color.LIGHT_GRAY, ConfirmingBrowserProvider)
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
                    color = Color.LIGHT_GRAY.toConstraint()
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

    override fun afterInitialization() {
        // Required since world selection screen doesn't automatically update
        if (type == ProjectType.WORLD) {
            forwardScreens.replaceAll { if (it is GuiSelectWorld) GuiSelectWorld((it as WorldSelectionScreenAccessor).parentScreen) else it }
            backScreens.replaceAll { if (it is GuiSelectWorld) GuiSelectWorld((it as WorldSelectionScreenAccessor).parentScreen) else it }
        }
    }
}