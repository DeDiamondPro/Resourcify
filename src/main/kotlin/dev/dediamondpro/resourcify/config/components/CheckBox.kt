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

package dev.dediamondpro.resourcify.config.components

import dev.dediamondpro.resourcify.gui.data.Colors
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import java.awt.Color

class CheckBox(private var enabled: Boolean) : UIContainer() {
    private val actions: MutableList<(Boolean) -> Unit> = mutableListOf()
    private val enabledColor = Colors.SECONDARY
    private val disabledColor = Color(Colors.SECONDARY.red, Colors.SECONDARY.green, Colors.SECONDARY.blue, 0)
    private val check = UIBlock(if (enabled) enabledColor else disabledColor).constrain {
        x = 2.pixels()
        y = 2.pixels()
        width = 10.pixels()
        height = 10.pixels()
    }.animateBeforeHide {
        setColorAnimation(
            Animations.IN_OUT_QUAD,
            0.15f,
            disabledColor.toConstraint(),
            0f
        )
    }.animateAfterUnhide {
        setColorAnimation(
            Animations.IN_OUT_QUAD,
            0.15f,
            enabledColor.toConstraint(),
            0f
        )
    } childOf this

    init {
        effect(OutlineEffect(Colors.SECONDARY, 1f))
        constrain {
            width = 14.pixels()
            height = 14.pixels()
        }
        onMouseClick {
            enabled = !enabled
            if (enabled) {
                check.unhide()
            } else {
                check.hide()
            }
            actions.forEach {
                it.invoke(enabled)
            }
        }
        if (!enabled) {
            check.hide(true)
        }
    }

    fun onToggle(action: (Boolean) -> Unit): CheckBox {
        actions.add(action)
        return this
    }
}