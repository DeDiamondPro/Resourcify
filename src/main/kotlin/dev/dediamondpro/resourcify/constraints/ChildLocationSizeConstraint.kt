/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.HeightConstraint
import gg.essential.elementa.constraints.PaddingConstraint
import gg.essential.elementa.constraints.WidthConstraint
import gg.essential.elementa.constraints.resolution.ConstraintVisitor

class ChildLocationSizeConstraint : WidthConstraint, HeightConstraint {
    override var cachedValue: Float = 0f
    override var constrainTo: UIComponent? = null
    override var recalculate: Boolean = true

    override fun getWidthImpl(component: UIComponent): Float {
        val constraint = constrainTo ?: component
        return ((constraint.children.maxOfOrNull {
            it.getRight() +
                    ((it.constraints.x as? PaddingConstraint)?.getHorizontalPadding(it) ?: 0f)
        } ?: 0f) - component.getLeft()).coerceAtLeast(0f)
    }

    override fun getHeightImpl(component: UIComponent): Float {
        val constraint = constrainTo ?: component
        return ((constraint.children.maxOfOrNull {
            it.getBottom() +
                    ((it.constraints.y as? PaddingConstraint)?.getVerticalPadding(it) ?: 0f)
        } ?: 0f) - component.getTop()).coerceAtLeast(0f)
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}
}