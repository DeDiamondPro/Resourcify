/*
 * This file is part of Resourcify
 * Copyright (C) 2024 DeDiamondPro
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

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.elements.DropDown
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.services.ServiceRegistry
import dev.dediamondpro.resourcify.util.localize
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import net.minecraft.client.gui.GuiScreen
import java.awt.Color

class SettingsPage() : PaginatedScreen(adaptScale = false) {

    //#if FORGE && MC <= 11202
    constructor(lastPage: GuiScreen) : this()
    //#endif

    init {
        val mainBox = UIContainer().constrain {
            x = CenterConstraint()
            width = min(692.pixels(), basicWidthConstraint { window.getWidth() - 8 })
            height = 100.percent()
        } childOf window
        UIText("resourcify.config.title".localize()).constrain {
            x = CenterConstraint()
            y = 8.pixels()
            textScale = 2.pixels()
        } childOf mainBox

        // Source
        val sourceBox = UIBlock(Color(0, 0, 0, 100)).constrain {
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
        } childOf sourceBox
        UIWrappedText("resourcify.config.source.title".localize()).constrain {
            width = 100.percent()
        } childOf sourceDescriptionBox
        UIWrappedText("resourcify.config.source.description".localize()).constrain {
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf sourceDescriptionBox
        DropDown(
            ServiceRegistry.getServices().map { it.getName() },
            true, mutableListOf(Config.instance.defaultService)
        ).constrain {
            x = 4.pixels(true)
            y = CenterConstraint()
            width = 160.pixels()
        }.onSelectionUpdate {
            Config.instance.defaultService = it.first()
            Config.save()
        } childOf sourceBox

        // Ads
        val adsBox = UIBlock(Color(0, 0, 0, 100)).constrain {
            x = 0.pixels()
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint() + 4.pixels()
        } childOf mainBox
        val adsDescriptionBox = UIContainer().constrain {
            x = 4.pixels()
            y = 4.pixels()
            width = 100.percent() - 168.pixels()
            height = ChildLocationSizeConstraint()
        } childOf adsBox
        UIWrappedText("resourcify.config.ads.title".localize()).constrain {
            width = 100.percent()
        } childOf adsDescriptionBox
        UIWrappedText("resourcify.config.ads.description".localize()).constrain {
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            color = Color.LIGHT_GRAY.toConstraint()
        } childOf adsDescriptionBox
        val checkBox = UIContainer().constrain {
            x = 4.pixels(alignOpposite = true)
            y = CenterConstraint()
            width = 14.pixels()
            height = 14.pixels()
        } childOf adsBox effect OutlineEffect(Color.LIGHT_GRAY, 1f)
        val check = UIBlock(Color(192, 192, 192, if (Config.instance.adsEnabled) 255 else 0)).constrain {
            x = 2.pixels()
            y = 2.pixels()
            width = 10.pixels()
            height = 10.pixels()
        }.animateBeforeHide {
            setColorAnimation(
                Animations.IN_OUT_QUAD,
                0.15f,
                Color(192, 192, 192, 0).toConstraint(),
                0f
            )
        }.animateAfterUnhide {
            setColorAnimation(
                Animations.IN_OUT_QUAD,
                0.15f,
                Color(192, 192, 192, 255).toConstraint(),
                0f
            )
        } childOf checkBox
        if (!Config.instance.adsEnabled) check.hide(true)
        checkBox.onMouseClick {
            val adsEnabled = !Config.instance.adsEnabled
            Config.instance.adsEnabled = adsEnabled
            Config.save()
            if (adsEnabled) check.unhide() else check.hide()
        }
    }
}