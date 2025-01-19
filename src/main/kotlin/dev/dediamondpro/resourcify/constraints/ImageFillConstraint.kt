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

package dev.dediamondpro.resourcify.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.HeightConstraint
import gg.essential.elementa.constraints.WidthConstraint
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import kotlin.math.max
import kotlin.math.min

class ImageFillConstraint(val type: FillType = FillType.FILL) : WidthConstraint, HeightConstraint {
    override var cachedValue: Float = 0f
    override var constrainTo: UIComponent? = null
    override var recalculate: Boolean = true

    override fun getWidthImpl(component: UIComponent): Float {
        val constraint = constrainTo ?: component.parent
        if (component !is UIImage) return constraint.getWidth()
        val scale = getScale(component, constraint)
        return component.imageWidth * scale
    }

    override fun getHeightImpl(component: UIComponent): Float {
        val constraint = constrainTo ?: component.parent
        if (component !is UIImage) return constraint.getHeight()
        val scale = getScale(component, constraint)
        return component.imageHeight * scale
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}

    private fun getScale(image: UIImage, parent: UIComponent): Float {
        if (type == FillType.FILL) return min(
            parent.getWidth() / image.imageWidth,
            parent.getHeight() / image.imageHeight
        )
        return max(parent.getWidth() / image.imageWidth, parent.getHeight() / image.imageHeight)
    }

    enum class FillType {
        FILL,
        CROP
    }
}