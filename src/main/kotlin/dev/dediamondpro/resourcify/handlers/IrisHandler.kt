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

package dev.dediamondpro.resourcify.handlers


import net.minecraft.client.gui.components.AbstractSelectionList
import net.minecraft.client.gui.screens.Screen
import java.lang.reflect.Field
import java.lang.reflect.Method

object IrisHandler {

    // TODO: convert back to mixin based when essential loom supports mixin remap thingy
    /*fun getActiveShader(screen: Screen): String? {
        if (screen !is ShaderPackScreen) return null
        return ((screen as IrisAccessor).shaderPackList.selected as ShaderPackEntry?)?.packName
    }

    fun applyShaders(screen: Screen, shader: String) {
        if (screen !is ShaderPackScreen) return
        val list = (screen as IrisAccessor).shaderPackList
        list.refresh()
        list.select(shader)
        screen.applyChanges()
    }*/

    private var initialized = false
    private var shaderPackListField: Field? = null
    private var screenApplyChangesMethod: Method? = null
    private var packRefreshMethod: Method? = null
    private var packSelectMethod: Method? = null
    private var packNameField: Field? = null

    init {
        createIrisHooks()
    }

    private fun createIrisHooks() {
        var irisPackage: String? = null
        var screenClass: Class<*>? = null
        try {
            screenClass = Class.forName("net.coderbot.iris.gui.screen.ShaderPackScreen")
            irisPackage = "net.coderbot.iris"
        } catch (_: Exception) {
            try {
                screenClass = Class.forName("net.irisshaders.iris.gui.screen.ShaderPackScreen")
                irisPackage = "net.irisshaders.iris"
            } catch (_: Exception) {
            }
        }
        if (irisPackage == null || screenClass == null) return
        try {
            val packListClass = Class.forName("$irisPackage.gui.element.ShaderPackSelectionList")
            val packEntryClass = Class.forName("$irisPackage.gui.element.ShaderPackSelectionList\$ShaderPackEntry")

            shaderPackListField = screenClass.getDeclaredField("shaderPackList")
            shaderPackListField!!.isAccessible = true
            screenApplyChangesMethod = screenClass.getDeclaredMethod("applyChanges")
            screenApplyChangesMethod!!.isAccessible = true

            packRefreshMethod = packListClass.getDeclaredMethod("refresh")
            packRefreshMethod!!.isAccessible = true
            packSelectMethod = packListClass.getDeclaredMethod("select", String::class.java)
            packSelectMethod!!.isAccessible = true

            packNameField = packEntryClass.getDeclaredField("packName")
            packNameField!!.isAccessible = true

            initialized = true
        } catch (_: Exception) {
        }
    }

    private fun getShaderPackList(screen: Screen): AbstractSelectionList<*>? {
        if (!initialized) return null
        return shaderPackListField?.get(screen) as AbstractSelectionList<*>?
    }

    fun getActiveShader(screen: Screen): String? {
        if (!initialized) return null
        val selected = getShaderPackList(screen)?.getSelected() ?: return null
        return packNameField?.get(selected) as String?
    }

    fun applyShaders(screen: Screen, shader: String) {
        if (!initialized) return
        val list = getShaderPackList(screen) ?: return
        packRefreshMethod?.invoke(list)
        packSelectMethod?.invoke(list, shader)
        screenApplyChangesMethod?.invoke(screen)
    }
}