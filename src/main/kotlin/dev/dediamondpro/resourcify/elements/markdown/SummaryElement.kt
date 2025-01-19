/*
 * This file is part of Resourcify
 * Copyright (C) 2024-2025 DeDiamondPro
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
import dev.dediamondpro.resourcify.elements.McImage
import dev.dediamondpro.resourcify.gui.data.Icons
import dev.dediamondpro.resourcify.util.Utils
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
        val isOpen = actualParent?.open == true
        val image = if (isOpen) openedImage else closedImage
        val imageWidth = if (isOpen) 7.0 else 8.0
        val imageHeight = if (isOpen) 8.0 else 7.0
        val realY = y + totalHeight / 2.0 - imageHeight / 2.0
        image.drawImage(
            matrixStack, x.toDouble() + 1.0, realY + 1.0,
            imageWidth, imageHeight, iconShadowColor
        )
        image.drawImage(
            matrixStack, x.toDouble(), realY,
            imageWidth, imageHeight, iconColor
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
        private val closedImage = McImage(Icons.EXPANDABLE_CLOSED)
        private val openedImage = McImage(Icons.EXPANDABLE_OPENED)
        private val iconColor = Color.WHITE
        private val iconShadowColor = Utils.getShadowColor(iconColor)
    }
}