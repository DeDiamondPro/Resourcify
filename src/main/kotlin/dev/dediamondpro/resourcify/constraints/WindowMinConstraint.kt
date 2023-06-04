/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
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