package dev.dediamondpro.resourcify.handlers


//#if MC >= 11802
//$$import net.minecraft.client.gui.widget.EntryListWidget
//#endif
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.list.ExtendedList
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

    private fun getShaderPackList(screen: Screen):
    //#if MC >= 11802
    //$$ EntryListWidget<*>?
    //#else
            ExtendedList<*>?
    //#endif
    {
        if (!initialized) return null
        return shaderPackListField?.get(screen) as
                //#if MC >= 11802
                //$$ EntryListWidget<*>?
                //#else
                ExtendedList<*>?
        //#endif
    }

    fun getActiveShader(screen: Screen): String? {
        if (!initialized) return null
        val selected = getShaderPackList(screen)?.selected ?: return null
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