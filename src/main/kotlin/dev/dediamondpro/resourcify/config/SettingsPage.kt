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
import dev.dediamondpro.resourcify.gui.browsepage.BrowseScreen
import dev.dediamondpro.resourcify.gui.data.Colors
import dev.dediamondpro.resourcify.services.ServiceRegistry
import dev.dediamondpro.resourcify.util.localize
import gg.essential.elementa.components.*
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect

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
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf mainBox

        // Source
        val allServices = ServiceRegistry.getAllServices().map { it.getName() }
        addDropdownOption("resourcify.config.source", allServices, Config.instance.defaultService) {
            Config.instance.defaultService = it
        }

        // GUI scale
        var default: Int? = Config.instance.guiScale
        if (default == -1) default = null
        addNumberInput("resourcify.config.gui-scale", "resourcify.config.gui-scale.auto", default) {
            Config.instance.guiScale = if (it == -1) it else it.coerceIn(1, 10)
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

        // Gifs
        addCheckBoxOption("resourcify.config.disable-gifs", Config.instance.gifsDisabled) {
            Config.instance.gifsDisabled = it
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

    override fun goBack() {
        val screen = backScreens.lastOrNull()
        if (screen is BrowseScreen) {
            // Replace the browse screens in case this screen was opened from the browse page
            // This makes sure the settings apply immediately
            backScreens.removeLast()
            forwardScreens.add(this)
            replaceScreen { BrowseScreen(screen.type, screen.downloadFolder, screen.service) }
        } else {
            super.goBack()
        }
    }

    private fun addCheckBoxOption(localizationString: String, enabled: Boolean, onUpdate: (Boolean) -> Unit) {
        val box = UIBlock(Colors.BACKGROUND).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint() + 4.pixels()
        } childOf mainBox
        val descriptionBox = UIContainer().constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = 100.percent() - 170.pixels()
            height = ChildLocationSizeConstraint()
        } childOf box
        UIWrappedText("$localizationString.title".localize()).constrain {
            width = 100.percent()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf descriptionBox
        UIWrappedText("$localizationString.description".localize()).constrain {
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            color = Colors.TEXT_SECONDARY.toConstraint()
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
        val box = UIBlock(Colors.BACKGROUND).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint() + 4.pixels()
        } childOf mainBox
        val sourceDescriptionBox = UIContainer().constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = 100.percent() - 170.pixels()
            height = ChildLocationSizeConstraint()
        } childOf box
        UIWrappedText("$localizationString.title".localize()).constrain {
            width = 100.percent()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf sourceDescriptionBox
        UIWrappedText("$localizationString.description".localize()).constrain {
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            color = Colors.TEXT_SECONDARY.toConstraint()
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

    private fun addNumberInput(
        localizationString: String,
        placeholder: String,
        default: Int?,
        onUpdate: (Int) -> Unit
    ) {
        val box = UIBlock(Colors.BACKGROUND).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint() + 4.pixels()
        } childOf mainBox
        val sourceDescriptionBox = UIContainer().constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = 100.percent() - 170.pixels()
            height = ChildLocationSizeConstraint()
        } childOf box
        UIWrappedText("$localizationString.title".localize()).constrain {
            width = 100.percent()
            color = Colors.TEXT_PRIMARY.toConstraint()
        } childOf sourceDescriptionBox
        UIWrappedText("$localizationString.description".localize()).constrain {
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            color = Colors.TEXT_SECONDARY.toConstraint()
        } childOf sourceDescriptionBox
        val textBox = UIBlock(Colors.DROPDOWN).constrain {
            x = 4.pixels(true)
            y = CenterConstraint()
            width = 160.pixels()
            height = 17.pixels()
        } effect OutlineEffect(Colors.DROPDOWN_BORDER, 1f) childOf box
        val textInput = UITextInput(placeholder.localize(), cursorColor = Colors.TEXT_SECONDARY).constrain {
            x = 4.pixels()
            y = CenterConstraint()
            width = 100.percent() - 8.pixels()
            color = Colors.TEXT_SECONDARY.toConstraint()
        } childOf textBox
        textInput.onUpdate {
            if (it.trim().isEmpty()) {
                onUpdate(-1)
                Config.save()
            } else {
                it.toIntOrNull()?.let { num ->
                    onUpdate(num)
                    Config.save()
                }
            }
        }.onMouseClick {
            if (it.mouseButton != 0) return@onMouseClick
            grabWindowFocus()
        }
        default?.let { textInput.setText(it.toString()) }
    }
}