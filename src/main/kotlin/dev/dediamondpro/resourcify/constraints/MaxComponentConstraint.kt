/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.constraints

import gg.essential.elementa.UIComponent
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.HeightConstraint
import gg.essential.elementa.constraints.SuperConstraint
import gg.essential.elementa.constraints.WidthConstraint
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import kotlin.math.max

class MaxComponentConstraint(
    private val normalConstraint: SuperConstraint<Float>,
) : WidthConstraint, HeightConstraint {
    override var cachedValue: Float = 0f
    override var constrainTo: UIComponent? = null
    override var recalculate: Boolean = true
    private var childConstraint: ChildConstraint? = null
    private var componentCached: UIComponent? = null

    fun createChildConstraint(normalConstraint: SuperConstraint<Float>): ChildConstraint {
        childConstraint = ChildConstraint(normalConstraint, this)
        return childConstraint!!
    }

    override fun getWidthImpl(component: UIComponent): Float {
        if (componentCached == null) childConstraint?.recalculate = true
        componentCached = component
        return childConstraint?.let {
            max(getWidthActual(), it.getWidthActual())
        } ?: getWidthActual()
    }

    private fun getWidthActual(): Float {
        if (componentCached == null) return 0f
        return (normalConstraint as WidthConstraint).getWidthImpl(componentCached!!)
    }

    override fun getHeightImpl(component: UIComponent): Float {
        if (componentCached == null) childConstraint?.recalculate = true
        componentCached = component
        return childConstraint?.let {
            max(getHeightActual(), it.getHeightActual())
        } ?: getHeightActual()
    }

    private fun getHeightActual(): Float {
        if (componentCached == null) return 0f
        return (normalConstraint as HeightConstraint).getHeightImpl(componentCached!!)
    }

    override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}

    inner class ChildConstraint(
        private val normalConstraint: SuperConstraint<Float>,
        private val parentConstraint: MaxComponentConstraint
    ) : WidthConstraint, HeightConstraint {
        override var cachedValue: Float = 0f
        override var constrainTo: UIComponent? = null
        override var recalculate: Boolean = true
        private var componentCached: UIComponent? = null

        override fun getWidthImpl(component: UIComponent): Float {
            if (componentCached == null) parentConstraint.recalculate = true
            componentCached = component
            return max(getWidthActual(), parentConstraint.getWidthActual())
        }

        fun getWidthActual(): Float {
            if (componentCached == null) return 0f
            return (normalConstraint as WidthConstraint).getWidthImpl(componentCached!!)
        }

        override fun getHeightImpl(component: UIComponent): Float {
            if (componentCached == null) parentConstraint.recalculate = true
            componentCached = component
            return max(getHeightActual(), parentConstraint.getHeightActual())
        }

        fun getHeightActual(): Float {
            if (componentCached == null) return 0f
            return (normalConstraint as HeightConstraint).getHeightImpl(componentCached!!)
        }

        override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}
    }
}