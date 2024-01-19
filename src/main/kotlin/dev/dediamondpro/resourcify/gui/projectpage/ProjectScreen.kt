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

package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.elementa.style.MarkdownTextStyle
import dev.dediamondpro.minemark.elementa.util.ElementaBrowserProvider
import dev.dediamondpro.minemark.style.LinkStyleConfig
import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.constraints.WindowMinConstraint
import dev.dediamondpro.resourcify.elements.Paginator
import dev.dediamondpro.resourcify.elements.TextIcon
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.gui.projectpage.components.MemberCard
import dev.dediamondpro.resourcify.modrinth.*
import dev.dediamondpro.resourcify.platform.Platform
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
import java.net.URL
import java.util.concurrent.CompletableFuture

class ProjectScreen(
    private val projectLimited: ProjectObject,
    val type: ApiInfo.ProjectType,
    val downloadFolder: File
) : PaginatedScreen() {
    val project = URL("${ApiInfo.API}/project/${projectLimited.slug}").getJsonAsync<ProjectResponse>()
    val versions = URL("${ApiInfo.API}/project/${projectLimited.slug}/version").getJsonAsync<List<Version>>()
    private val members: CompletableFuture<List<Member>?> =
        URL("${ApiInfo.API}/project/${projectLimited.slug}/members").getJsonAsync<List<Member>>().thenApply {
            it?.sortedBy { member -> member.ordering }
        }
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

        versions.thenAccept { versions ->
            if (versions == null) return@thenAccept
            val versionToDownload = versions.firstOrNull {
                it.gameVersions.contains(Platform.getMcVersion()) && it.loaders.contains(type.loader)
            } ?: return@thenAccept
            val fileToDownload = versionToDownload.files.firstOrNull {
                it.primary
            } ?: versionToDownload.files.firstOrNull() ?: return@thenAccept
            val url = URL(fileToDownload.url)
            var installed = packHashes.get().contains(fileToDownload.hashes.sha512)
            val buttonText = if (installed) "${ChatColor.BOLD}${localize("resourcify.version.installed")}"
            else "${ChatColor.BOLD}${localize("resourcify.version.install_version", versionToDownload.versionNumber)}"
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
                        DownloadManager.download(
                            File(downloadFolder, fileToDownload.fileName),
                            fileToDownload.hashes.sha512, url
                        ) {
                            text?.setText("${ChatColor.BOLD}${localize("resourcify.version.installed")}")
                            installed = true
                        }
                        progressBox?.constraints?.width?.recalculate = true
                    } else {
                        DownloadManager.cancelDownload(url)
                        text?.setText(
                            "${ChatColor.BOLD}${
                                localize("resourcify.version.install_version", versionToDownload.versionNumber)
                            }"
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
        project.thenAccept { projects ->
            if (projects == null) return@thenAccept
            loadedPages[::DescriptionPage] = DescriptionPage(this) childOf mainBox
        }
        mapOf<String, (ProjectScreen) -> UIComponent>(
            "resourcify.project.description".localize() to ::DescriptionPage,
            "resourcify.project.gallery".localize() to ::GalleryPage,
            "resourcify.project.versions".localize() to ::VersionsPage
        ).forEach { (text, page) ->
            if (text == "resourcify.project.gallery".localize() && projectLimited.gallery.isEmpty()) return@forEach
            UIText("${ChatColor.BOLD}$text").constrain {
                x = if (text == "resourcify.project.description".localize()) 6.pixels()
                else SiblingConstraint(padding = 8f)
                y = CenterConstraint()
            }.onMouseClick {
                if (page == currentPage || !project.isDone || !versions.isDone || it.mouseButton != 0) return@onMouseClick
                currentPage = page
                loadedPages.forEach { page -> page.value.hide() }
                loadedPages.getOrPut(page) {
                    page(this@ProjectScreen) childOf mainBox
                }.unhide()
            } childOf navigationBox
        }
        TextIcon("${ChatColor.BOLD}${localize("resourcify.project.modrinth")}", Icons.EXTERNAL_LINK).constrain {
            x = SiblingConstraint(padding = 8f)
            y = CenterConstraint()
            width = ChildLocationSizeConstraint()
            height = ChildBasedMaxSizeConstraint()
        }.onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            println(projectLimited.browserUrl)
            UDesktop.browse(URI(projectLimited.browserUrl))
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
        val bannerUrl = projectLimited.featuredGallery
        if (bannerUrl != null) UIImage.ofURL(bannerUrl, false).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = ImageAspectConstraint()
        } childOf sideBox
        if (projectLimited.iconUrl.isNullOrBlank()) UIImage.ofResource("/pack.png") else UIImage.ofURL(projectLimited.iconUrl)
            .constrain {
                x = 4.pixels()
                y = SiblingConstraint(padding = 4f)
                width = 64.pixels()
                height = 64.pixels()
            } childOf sideBox
        UIWrappedText(projectLimited.title).constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 152.pixels()
            textScale = 1.5.pixels()
        } childOf sideBox
        UIWrappedText(projectLimited.description).constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 152.pixels()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf sideBox
        UIText("resourcify.project.categories".localize()).constrain {
            x = 4.pixels()
            y = SiblingConstraint(padding = 8f)
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf sideBox
        projectLimited.categories.forEach {
            UIText(
                "- ${
                    localizeOrDefault(
                        "resourcify.categories.${projectLimited.projectType}.${
                            it.lowercase().replace(" ", "_")
                        }", it.capitalizeAll()
                    )
                }"
            ).constrain {
                x = 4.pixels()
                y = SiblingConstraint(padding = 2f)
                color = Color.LIGHT_GRAY.toConstraint()
            } childOf sideBox
        }
        val externalResourcesBox = UIContainer().constrain {
            x = 0.pixels()
            y = SiblingConstraint()
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        } childOf sideBox
        project.thenAccept { project ->
            if (project == null) return@thenAccept
            val links = mutableListOf<String>()
            with(links) {
                if (project.issuesUrl != null) add("[${localize("resourcify.project.issues")}](${project.issuesUrl})")
                if (project.sourceUrl != null) add("[${localize("resourcify.project.source")}](${project.sourceUrl})")
                if (project.wikiUrl != null) add("[${localize("resourcify.project.wiki")}](${project.wikiUrl})")
                if (project.discordUrl != null) add("[${localize("resourcify.project.discord")}](${project.discordUrl})")
                addAll(project.donationUrls.map { "[${it.platform}](${it.url})" })
            }
            Window.enqueueRenderOperation {
                if (links.isNotEmpty()) {
                    externalResourcesBox.constrain { y = SiblingConstraint(padding = 8f) }
                    UIText("resourcify.project.external_resources".localize()).constrain {
                        x = 4.pixels()
                        y = 0.pixels()
                        color = Color.LIGHT_GRAY.toConstraint()
                    } childOf externalResourcesBox
                    markdown(
                        links.joinToString(" * "),
                        style = MarkdownStyle(
                            textStyle = MarkdownTextStyle(
                                1f, Color.LIGHT_GRAY, 1f, DefaultFonts.VANILLA_FONT_RENDERER
                            ),
                            linkStyle = LinkStyleConfig(Color.LIGHT_GRAY, ElementaBrowserProvider)
                        ),
                    ).constrain {
                        x = 4.pixels()
                        y = SiblingConstraint(padding = 2f)
                        width = 100.percent() - 8.pixels()
                    } childOf externalResourcesBox
                } else {
                    sideBox.removeChild(externalResourcesBox)
                }
            }
        }
        val membersBox = UIContainer().constrain {
            x = 0.pixels()
            y = SiblingConstraint()
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        } childOf sideBox
        members.thenAccept { members ->
            if (members.isNullOrEmpty()) return@thenAccept
            Window.enqueueRenderOperation {
                membersBox.constrain { y = SiblingConstraint(padding = 8f) }
                UIText("resourcify.project.members".localize()).constrain {
                    x = 4.pixels()
                    y = 0.pixels()
                    color = Color.LIGHT_GRAY.toConstraint()
                } childOf membersBox
                for (member in members) {
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