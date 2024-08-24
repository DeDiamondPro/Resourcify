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
import dev.dediamondpro.minemark.elementa.elements.MarkdownTextComponent
import dev.dediamondpro.minemark.elementa.style.MarkdownStyle
import dev.dediamondpro.minemark.elements.ChildMovingElement
import dev.dediamondpro.minemark.elements.Element
import dev.dediamondpro.minemark.elements.creators.ElementCreator
import dev.dediamondpro.minemark.utils.MouseButton
import gg.essential.elementa.components.UIBlock
import gg.essential.universal.UMatrixStack
import org.xml.sax.Attributes
import java.awt.Color

@Suppress("UnstableApiUsage")
class ExpandableMarkdownElement(
    style: MarkdownStyle,
    layoutStyle: LayoutStyle,
    parent: Element<MarkdownStyle, UMatrixStack>?,
    qName: String, attributes: Attributes?
) : ChildMovingElement<MarkdownStyle, UMatrixStack>(style, layoutStyle, parent, qName, attributes) {
    var open: Boolean = false

    init {
        if (qName == "details") {
            open = attributes?.getValue("open") != null
        } else if (qName == "div" && attributes?.getValue("class") == "spoiler") {
            SummaryElement(style, layoutStyle, this, "summary", attributes).apply {
                MarkdownTextComponent("Show Spoiler", style, layoutStyle, this, "", attributes)
            }
        }
    }

    override fun generateNewLayout(layoutData: LayoutData?, renderData: UMatrixStack?) {
        val summary = children.firstOrNull { it is SummaryElement } as? SummaryElement?
        summary?.generateLayoutInternal(layoutData, renderData)
        if (!open) return
        children.forEach { if (it != summary) it.generateLayoutInternal(layoutData, renderData) }
    }

    override fun drawInternal(xOffset: Float, yOffset: Float, mouseX: Float, mouseY: Float, renderData: UMatrixStack) {
        val actualX = xOffset + extraXOffset
        val actualY = yOffset + extraYOffset
        val actualMouseX = mouseX - extraXOffset
        val actualMouseY = mouseY - extraYOffset
        marker?.let { drawMarker(it.x + xOffset, it.y + yOffset, it.width, it.height, renderData) }
        val summary = children.firstOrNull { it is SummaryElement } as? SummaryElement?
        summary?.drawInternal(actualX, actualY, actualMouseX, actualMouseY, renderData)
        if (!open) return
        children.forEach {
            if (it != summary) it.drawInternal(
                actualX, actualY, actualMouseX, actualMouseY, renderData
            )
        }
    }

    override fun beforeDrawInternal(
        xOffset: Float, yOffset: Float, mouseX: Float, mouseY: Float, renderData: UMatrixStack?
    ) {
        val actualX = xOffset + extraXOffset
        val actualY = yOffset + extraYOffset
        val actualMouseX = mouseX - extraXOffset
        val actualMouseY = mouseY - extraYOffset
        val summary = children.firstOrNull { it is SummaryElement } as? SummaryElement?
        summary?.beforeDrawInternal(actualX, actualY, actualMouseX, actualMouseY, renderData)
        if (!open) return
        children.forEach {
            if (it != summary) it.beforeDrawInternal(
                actualX, actualY, actualMouseX, actualMouseY, renderData
            )
        }
    }

    override fun onMouseClickedInternal(button: MouseButton, mouseX: Float, mouseY: Float) {
        val actualMouseX = mouseX - extraXOffset
        val actualMouseY = mouseY - extraYOffset
        // Get this here to avoid the summary element opening and the click event getting passed along before
        // a new layout was generated
        val wasOpen = open
        val summary = children.firstOrNull { it is SummaryElement } as? SummaryElement?
        summary?.onMouseClickedInternal(button, actualMouseX, actualMouseY)
        if (!wasOpen) return
        children.forEach { if (it != summary) it.onMouseClickedInternal(button, actualMouseX, actualMouseY) }
    }

    override fun drawMarker(x: Float, y: Float, markerWidth: Float, totalHeight: Float, matrixStack: UMatrixStack) {
        UIBlock.drawBlockSized(
            matrixStack, BLOCK_COLOR,
            x.toDouble(), y.toDouble(),
            markerWidth.toDouble(), totalHeight.toDouble()
        )
    }


    override fun getMarkerType(): MarkerType {
        return MarkerType.BLOCK
    }

    override fun getMarkerWidth(layoutData: LayoutData?, renderData: UMatrixStack?): Float {
        return 0f
    }

    override fun getOutsidePadding(layoutData: LayoutData?, renderData: UMatrixStack?): Float {
        return 6f
    }

    override fun getInsidePadding(layoutData: LayoutData?, renderData: UMatrixStack?): Float {
        return 6f
    }

    object ExpandableElementCreator : ElementCreator<MarkdownStyle, UMatrixStack> {
        override fun createElement(
            style: MarkdownStyle,
            layoutStyle: LayoutStyle,
            parent: Element<MarkdownStyle, UMatrixStack>,
            qName: String,
            attributes: Attributes
        ): Element<MarkdownStyle, UMatrixStack> {
            return ExpandableMarkdownElement(style, layoutStyle, parent, qName, attributes)
        }

        override fun appliesTo(
            style: MarkdownStyle?,
            layoutStyle: LayoutStyle,
            parent: Element<MarkdownStyle, UMatrixStack>,
            qName: String,
            attributes: Attributes
        ): Boolean {
            return qName == "details" || (qName == "div" && attributes.getValue("class") == "spoiler")
        }
    }

    companion object {
        private val BLOCK_COLOR = Color(0, 0, 0, 150)
    }
}