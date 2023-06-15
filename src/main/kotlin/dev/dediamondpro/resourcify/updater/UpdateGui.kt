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

package dev.dediamondpro.resourcify.updater

import dev.dediamondpro.resourcify.ModInfo
import dev.dediamondpro.resourcify.config.Config
import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.elements.MinecraftButton
import dev.dediamondpro.resourcify.modrinth.Version
import dev.dediamondpro.resourcify.modrinth.VersionFile
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.MinConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.markdown.MarkdownComponent
import gg.essential.universal.UDesktop
import java.net.URI
import kotlin.math.min

class UpdateGui(version: Version, file: VersionFile) : WindowScreen(ElementaVersion.V2) {
    init {
        val scrollBox = ScrollComponent(pixelsPerScroll = 30f, scrollAcceleration = 1.5f).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 100.percent()
        } childOf window
        val holder = UIContainer().constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        } childOf scrollBox

        UIText("A new version of ${ModInfo.NAME} is available!").constrain {
            x = CenterConstraint()
            y = 16.pixels()
            textScale = basicTextScaleConstraint { min(3f, (window.getWidth() - 4) / (it as UIText).getTextWidth()) }
        } childOf holder
        MarkdownComponent(version.changelog).constrain {
            x = CenterConstraint()
            y = SiblingConstraint(padding = 8f)
            width = MinConstraint((100.percent() boundTo window) - 4.pixels(), 600.pixels())
        } childOf holder
        val buttonBox = UIContainer().constrain {
            x = CenterConstraint()
            y = SiblingConstraint(padding = 8f)
            width = MinConstraint((100.percent() boundTo window) - 4.pixels(), 600.pixels())
            height = ChildBasedMaxSizeConstraint()
        } childOf holder
        MinecraftButton("Download Update").constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = MinConstraint((100.percent() boundTo window) - 4.pixels(), 600.pixels()) / 3 - 2.pixels()
            height = 20.pixels()
        }.onMouseClick {
            UDesktop.browse(URI("https://modrinth.com/mod/${version.projectId}/version/${version.id}"))
            displayScreen(null)
        } childOf buttonBox
        MinecraftButton("Close").constrain {
            x = CenterConstraint()
            y = 0.pixels()
            width = MinConstraint((100.percent() boundTo window) - 4.pixels(), 600.pixels()) / 3 - 2.pixels()
            height = 20.pixels()
        }.onMouseClick {
            displayScreen(null)
        } childOf buttonBox
        MinecraftButton("Ignore Update").constrain {
            x = 0.pixels(true)
            y = 0.pixels()
            width = MinConstraint((100.percent() boundTo window) - 4.pixels(), 600.pixels()) / 3 - 2.pixels()
            height = 20.pixels()
        }.onMouseClick {
            Config.INSTANCE.ignoredVersions.add(file.hashes.sha512)
            Config.save()
            displayScreen(null)
        } childOf buttonBox
    }
}