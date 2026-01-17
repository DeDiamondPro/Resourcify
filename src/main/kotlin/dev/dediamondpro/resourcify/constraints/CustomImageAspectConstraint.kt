/*
 * This file is part of Resourcify
 * Copyright (C) 2026 DeDiamondPro
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
import java.lang.UnsupportedOperationException

class CustomImageAspectConstraint : WidthConstraint, HeightConstraint {
    override var cachedValue = 0f
    override var recalculate = true
    override var constrainTo: UIComponent? = null

    private fun getImageWidthHeight(component: UIComponent): Pair<Float, Float> {
        when (component) {
            is UIImage -> return component.imageWidth to component.imageHeight
            is IUIImage -> return component.imageWidth to component.imageHeight
        }
        throw IllegalStateException("CustomImageAspectConstraint can only be used in IUIImage components")
    }

    override fun getWidthImpl(component: UIComponent): Float {
        val imageSize = getImageWidthHeight(component)
        return component.getHeight() * imageSize.first / imageSize.second
    }

    override fun getHeightImpl(component: UIComponent): Float {
        val imageSize = getImageWidthHeight(component)
        return component.getWidth() * imageSize.second / imageSize.first
    }

    override fun to(component: UIComponent) = apply {
        throw UnsupportedOperationException("Constraint.to(UIComponent) is not available in this context!")
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {
        when (type) {
            ConstraintType.WIDTH -> visitor.visitSelf(ConstraintType.HEIGHT)
            ConstraintType.HEIGHT -> visitor.visitSelf(ConstraintType.WIDTH)
            else -> throw IllegalArgumentException(type.prettyName)
        }
    }
}