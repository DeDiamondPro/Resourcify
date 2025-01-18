/*
 * This file is part of Resourcify
 * Copyright (C) 2024-2025 DeDiamondPro
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

package dev.dediamondpro.resourcify.config

import dev.dediamondpro.resourcify.config.components.CheckBox
import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.elements.DropDown
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.services.ServiceRegistry
import dev.dediamondpro.resourcify.util.localize
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import java.awt.Color

class SettingsPage : PaginatedScreen(adaptScale = false) {
    private val scrollBox = ScrollComponent(pixelsPerScroll = 30f, scrollAcceleration = 1.5f).constrain {
        width = 100.percent()
        height = 100.percent()
    } childOf window
    private val mainBox = UIContainer().constrain {
        x = CenterConstraint()
        width = min(692.pixels(), basicWidthConstraint { window.getWidth() - 8 })
        height = ChildLocationSizeConstraint() + 4.pixels()
    } childOf scrollBox

    init {
        UIText("resourcify.config.title".localize()).constrain {
            x = CenterConstraint()
            y = 8.pixels()
            textScale = 2.pixels()
        } childOf mainBox

        // Source
        val allServices = ServiceRegistry.getAllServices().map { it.getName() }
        addDropdownOption("resourcify.config.source", allServices, Config.instance.defaultService) {
            Config.instance.defaultService = it
        }

        // Thumbnail quality
        addCheckBoxOption("resourcify.config.thumbnail", Config.instance.fullResThumbnail) {
            Config.instance.fullResThumbnail = it
        }

        // Open links in Resourcify
        addCheckBoxOption("resourcify.config.open_link", Config.instance.openLinkInResourcify) {
            Config.instance.openLinkInResourcify = it
        }

        // Ads
        addCheckBoxOption("resourcify.config.ads", Config.instance.adsEnabled) {
            Config.instance.adsEnabled = it
        }

        // Types
        addCheckBoxOption("resourcify.config.resource-pack", Config.instance.resourcePacksEnabled) {
            Config.instance.resourcePacksEnabled = it
        }
        addCheckBoxOption("resourcify.config.data-pack", Config.instance.dataPacksEnabled) {
            Config.instance.dataPacksEnabled = it
        }
        addCheckBoxOption("resourcify.config.shader-pack", Config.instance.shaderPacksEnabled) {
            Config.instance.shaderPacksEnabled = it
        }
        addCheckBoxOption("resourcify.config.world", Config.instance.worldsEnabled) {
            Config.instance.worldsEnabled = it
        }
    }

    private fun addCheckBoxOption(localizationString: String, enabled: Boolean, onUpdate: (Boolean) -> Unit) {
        val box = UIBlock(Color(0, 0, 0, 100)).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint() + 4.pixels()
        } childOf mainBox
        val descriptionBox = UIContainer().constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = 100.percent() - 168.pixels()
            height = ChildLocationSizeConstraint()
        } childOf box
        UIWrappedText("$localizationString.title".localize()).constrain {
            width = 100.percent()
        } childOf descriptionBox
        UIWrappedText("$localizationString.description".localize()).constrain {
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf descriptionBox
        CheckBox(enabled).constrain {
            x = 4.pixels(alignOpposite = true)
            y = CenterConstraint()
        }.onToggle {
            onUpdate.invoke(it)
            Config.save()
        } childOf box
    }

    private fun addDropdownOption(
        localizationString: String,
        options: List<String>,
        selectedOption: String,
        onUpdate: (String) -> Unit
    ) {
        val box = UIBlock(Color(0, 0, 0, 100)).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint() + 4.pixels()
        } childOf mainBox
        val sourceDescriptionBox = UIContainer().constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = 100.percent() - 168.pixels()
            height = ChildLocationSizeConstraint()
        } childOf box
        UIWrappedText("$localizationString.title".localize()).constrain {
            width = 100.percent()
        } childOf sourceDescriptionBox
        UIWrappedText("$localizationString.description".localize()).constrain {
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf sourceDescriptionBox
        DropDown(
            options, true, mutableListOf(
                if (options.contains(selectedOption)) selectedOption else options.first()
            )
        ).constrain {
            x = 4.pixels(true)
            y = CenterConstraint()
            width = 160.pixels()
        }.onSelectionUpdate {
            onUpdate(it.first())
            Config.save()
        } childOf box
    }
}