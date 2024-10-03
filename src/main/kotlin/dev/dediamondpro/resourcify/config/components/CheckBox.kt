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

package dev.dediamondpro.resourcify.config.components

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import java.awt.Color

class CheckBox(private var enabled: Boolean) : UIContainer() {
    private val actions: MutableList<(Boolean) -> Unit> = mutableListOf()
    private val check = UIBlock(Color(192, 192, 192, if (enabled) 255 else 0)).constrain {
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
    } childOf this

    init {
        effect(OutlineEffect(Color.LIGHT_GRAY, 1f))
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