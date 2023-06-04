/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.elements

import dev.dediamondpro.resourcify.util.Utils
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.constraints.ConstraintType
import gg.essential.elementa.constraints.resolution.ConstraintVisitor
import gg.essential.elementa.dsl.*
import gg.essential.elementa.utils.ResourceCache
import java.awt.Color

class ShadowImage(
    asset: String,
    cache: ResourceCache? = null,
    imageColor: ColorConstraint = Color.WHITE.toConstraint(),
    shadowColor: ColorConstraint = ShadowColorConstraint(imageColor),
) : UIContainer() {
    init {
        createImage(asset, cache).constrain {
            x = 1.pixels()
            y = 1.pixels()
            width = 100.percent()
            height = 100.percent()
            color = shadowColor
        } childOf this
        createImage(asset, cache).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 100.percent()
            color = imageColor
        } childOf this
    }

    private fun createImage(asset: String, cache: ResourceCache?): UIImage {
        return cache?.let { UIImage.ofResourceCached(asset, cache) } ?: UIImage.ofResource(asset)
    }

    private class ShadowColorConstraint(val normalColor: ColorConstraint) : ColorConstraint {
        override var cachedValue: Color = Color.WHITE
        override var constrainTo: UIComponent? = null
        override var recalculate: Boolean = true

        override fun getColorImpl(component: UIComponent): Color {
            return Utils.getShadowColor(normalColor.getColor(component))
        }

        override fun visitImpl(visitor: ConstraintVisitor, type: ConstraintType) {}
    }
}