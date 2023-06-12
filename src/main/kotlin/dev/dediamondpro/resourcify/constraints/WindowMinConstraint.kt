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

package dev.dediamondpro.resourcify.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.WidthConstraint
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import kotlin.math.min

class WindowMinConstraint(private val minWidth: WidthConstraint): WidthConstraint {
    override var cachedValue: Float = 0f
    override var constrainTo: UIComponent? = null
    override var recalculate: Boolean = true

    override fun getWidthImpl(component: UIComponent): Float {
        val supperConstraint = constrainTo ?: component.parent
        val window = Window.of(component)

        val spaceUsed = supperConstraint.children.sumOf {
            if(it == component) 0.0 else it.getWidth().toDouble()
        }.toFloat()

        return min(window.getWidth() - spaceUsed, minWidth.getWidth(component))
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}
}