package dev.dediamondpro.resourcify.handlers

import dev.dediamondpro.resourcify.mixins.IrisAccessor
import net.coderbot.iris.gui.element.ShaderPackSelectionList.ShaderPackEntry
import net.coderbot.iris.gui.screen.ShaderPackScreen
import net.minecraft.client.gui.screen.Screen

object IrisHandler {

    fun getActiveShader(screen: Screen): String? {
        if (screen !is ShaderPackScreen) return null
        return ((screen as IrisAccessor).shaderPackList.selected as ShaderPackEntry?)?.packName
    }

    fun applyShaders(screen: Screen, shader: String) {
        if (screen !is ShaderPackScreen) return
        val list = (screen as IrisAccessor).shaderPackList
        list.refresh()
        list.select(shader)
        list.applied
        screen.applyChanges()
    }
}