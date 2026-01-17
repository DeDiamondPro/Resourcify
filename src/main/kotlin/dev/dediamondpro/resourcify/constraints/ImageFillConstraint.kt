/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2026 DeDiamondPro
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

import dev.dediamondpro.resourcify.elements.image.IUIImage
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

        var imageWidth: Float
        val scale = when (component) {
            is UIImage -> {
                imageWidth = component.imageWidth
                getScale(component.imageWidth, component.imageHeight, component.parent)
            }

            is IUIImage -> {
                imageWidth = component.imageWidth
                getScale(component.imageWidth, component.imageHeight, component.parent)
            }

            else -> return constraint.getWidth()
        }
        return imageWidth * scale
    }

    override fun getHeightImpl(component: UIComponent): Float {
        val constraint = constrainTo ?: component.parent

        var imageHeight: Float
        val scale = when (component) {
            is UIImage -> {
                imageHeight = component.imageHeight
                getScale(component.imageWidth, component.imageHeight, component.parent)
            }

            is IUIImage -> {
                imageHeight = component.imageHeight
                getScale(component.imageWidth, component.imageHeight, component.parent)
            }

            else -> return constraint.getWidth()
        }
        return imageHeight * scale
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}

    private fun getScale(width: Float, height: Float, parent: UIComponent): Float {
        if (type == FillType.FILL) return min(
            parent.getWidth() / width,
            parent.getHeight() / height
        )
        return max(parent.getWidth() / width, parent.getHeight() / height)
    }

    enum class FillType {
        FILL,
        CROP
    }
}