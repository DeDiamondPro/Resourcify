/*
 * This file is part of Resourcify
 * Copyright (C) 2023-2024 DeDiamondPro
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

package dev.dediamondpro.resourcify.elements

import dev.dediamondpro.resourcify.util.Icons
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UResolution
import java.awt.Color

class DropDown(
    val options: List<String>,
    private val onlyOneOption: Boolean = false,
    val selectedOptions: MutableList<String> = mutableListOf(),
    private val placeHolder: String = "",
    private val canDeSelect: Boolean = !onlyOneOption
) : UIContainer() {
    private val selectionUpdateListeners = mutableListOf<DropDown.(List<String>) -> Unit>()
    private var canOpen = false

    private val box = UIBlock(color = Color(0, 0, 0, 200)).constrain {
        x = 0.pixels()
        y = 0.pixels()
        width = 100.percent()
        height = ChildBasedMaxSizeConstraint() + 8.pixels()
    } effect OutlineEffect(Color.LIGHT_GRAY, 1f) childOf this

    private val text = UIWrappedText().constrain {
        x = 4.pixels()
        y = 4.pixels()
        width = 100.percent() - 21.pixels()
        color = Color.LIGHT_GRAY.toConstraint()
    } childOf box

    private val dropDownImage = Icon(Icons.DROPDOWN, true, Color.LIGHT_GRAY).constrain {
        x = 4.pixels(alignOpposite = true)
        y = CenterConstraint()
        width = 9.pixels()
        height = 9.pixels()
    } childOf box

    init {
        constrain { height = RelativeConstraint() boundTo box }
        updateText()
        val expandContainer = UIContainer().constrain {
            x = 0.pixels()
            y = basicYConstraint {
                if (this@DropDown.getBottom() > UResolution.scaledHeight - 150f) {
                    this@DropDown.getTop() - getHeight() - 1f
                } else {
                    this@DropDown.getTop() + box.getHeight() + 1f
                }
            }
            width = 100.percent()
            height = ChildBasedSizeConstraint()
        } effect OutlineEffect(Color.LIGHT_GRAY, 1f) childOf this
        val scrollBox = ScrollComponent(
            pixelsPerScroll = 30f,
            scrollAcceleration = 1.5f,
            customScissorBoundingBox = expandContainer
        ).constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 150.pixels()
        } childOf expandContainer
        scrollBox.setHeight(150.pixels() coerceAtMost (ChildBasedSizeConstraint() boundTo scrollBox.children.first()))
        expandContainer.hide(instantly = true)
        for (option in options) {
            DropDownElement(option, selectedOptions.contains(option)).constrain {
                x = 0.pixels()
                y = SiblingConstraint()
                width = 100.percent()
            }.onMouseClick {
                if (this !is DropDownElement || it.mouseButton != 0) return@onMouseClick
                if (selectedOptions.contains(option) && canDeSelect) {
                    selectedOptions.remove(option)
                } else if (onlyOneOption) {
                    selectedOptions.clear()
                    selectedOptions.add(option)
                    for (child in scrollBox.children.first().children) {
                        if (child !is DropDownElement) continue
                        if (child.option == option) continue
                        child.unSelectInstant()
                    }
                } else {
                    selectedOptions.add(option)
                }
                updateText()
                selectionUpdateListeners.forEach { listener -> listener(selectedOptions) }
            } childOf scrollBox
        }
        var hidden = true
        box.onMouseClick {
            if (!hidden || it.mouseButton != 0 || !canOpen) return@onMouseClick
            expandContainer.setFloating(true)
            expandContainer.unhide()
            expandContainer.grabWindowFocus()
            hidden = false
        }
        expandContainer.onMouseClick { if (canDeSelect || it.mouseButton != 0) grabWindowFocus() }
        expandContainer.onFocusLost {
            expandContainer.setFloating(false)
            expandContainer.hide()
            hidden = true
        }
    }

    fun updateText() {
        text.setText(selectedOptions.joinToString().ifBlank { placeHolder })
    }

    fun onSelectionUpdate(listener: DropDown.(List<String>) -> Unit): DropDown {
        selectionUpdateListeners.add(listener)
        return this
    }

    override fun draw(matrixStack: UMatrixStack) {
        super.draw(matrixStack)
        canOpen = true
    }

    inner class DropDownElement(
        val option: String, var selected: Boolean
    ) : UIBlock(color = if (selected) Color(27, 217, 106, 200) else Color(0, 0, 0, 220)) {
        init {
            constrain {
                height = 17.pixels()
            } effect ScissorEffect()
            UIText(option).constrain {
                x = 4.pixels()
                y = CenterConstraint()
                color = Color.LIGHT_GRAY.toConstraint()
            } childOf this
            onMouseClick {
                if (it.mouseButton != 0) return@onMouseClick
                if (!selected) select() else if (canDeSelect) unSelect()
            }
        }

        private fun select() {
            if (!onlyOneOption) animate {
                setColorAnimation(Animations.IN_OUT_QUAD, 0.2f, Color(27, 217, 106, 220).toConstraint())
            } else setColor(Color(27, 217, 106, 220).toConstraint())
            selected = true
        }

        private fun unSelect() {
            animate {
                setColorAnimation(Animations.IN_OUT_QUAD, 0.2f, Color(0, 0, 0, 220).toConstraint())
            }
            selected = false
        }

        fun unSelectInstant() {
            setColor(Color(0, 0, 0, 220))
            selected = false
        }
    }
}