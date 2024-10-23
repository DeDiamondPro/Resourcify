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

package dev.dediamondpro.resourcify.elements.markdown

import dev.dediamondpro.minemark.LayoutData
import dev.dediamondpro.minemark.LayoutStyle
import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.elements.ChildMovingElement
import dev.dediamondpro.minemark.elements.Element
import dev.dediamondpro.minemark.utils.MouseButton
import dev.dediamondpro.resourcify.util.Utils
import dev.dediamondpro.resourcify.util.ofResourceCustom
import gg.essential.elementa.components.UIImage
import gg.essential.universal.UMatrixStack
import org.xml.sax.Attributes
import java.awt.Color

@Suppress("UnstableApiUsage")
class SummaryElement(
    style: MarkdownStyle,
    layoutStyle: LayoutStyle,
    parent: Element<MarkdownStyle, UMatrixStack>?,
    qName: String, attributes: Attributes?
) : ChildMovingElement<MarkdownStyle, UMatrixStack>(style, layoutStyle, parent, qName, attributes) {
    private val actualParent = parent as? ExpandableMarkdownElement

    override fun drawMarker(x: Float, y: Float, markerWidth: Float, totalHeight: Float, matrixStack: UMatrixStack) {
        val image = if (actualParent?.open == true) openedImage else closedImage
        val realY = y + totalHeight / 2f - image.imageHeight / 2f
        image.drawImage(
            matrixStack, x.toDouble() + 1.0, realY.toDouble() + 1.0,
            image.imageWidth.toDouble(), image.imageHeight.toDouble(), iconShadowColor
        )
        image.drawImage(
            matrixStack, x.toDouble(), realY.toDouble(),
            image.imageWidth.toDouble(), image.imageHeight.toDouble(), iconColor
        )
    }

    override fun getMarkerWidth(layoutData: LayoutData?, renderData: UMatrixStack?): Float {
        return 12f
    }

    override fun onMouseClickedInternal(button: MouseButton, mouseX: Float, mouseY: Float) {
        if (button == MouseButton.LEFT && marker != null && mouseY >= marker.y && mouseY < marker.y + marker.height) {
            actualParent?.open = actualParent?.open?.not() ?: false
            regenerateLayout()
        } else {
            super.onMouseClickedInternal(button, mouseX, mouseY)
        }
    }

    companion object {
        private val closedImage = UIImage.ofResourceCustom("/assets/resourcify/expandable-closed.png", loadSync = true)
        private val openedImage = UIImage.ofResourceCustom("/assets/resourcify/expandable-opened.png", loadSync = true)
        private val iconColor = Color.WHITE
        private val iconShadowColor = Utils.getShadowColor(iconColor)
    }
}