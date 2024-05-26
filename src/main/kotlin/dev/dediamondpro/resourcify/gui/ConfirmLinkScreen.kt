package dev.dediamondpro.resourcify.gui

import dev.dediamondpro.resourcify.util.toURI
import gg.essential.universal.UDesktop
import gg.essential.universal.UKeyboard
import gg.essential.universal.UScreen
import net.minecraft.client.gui.GuiScreen

//#if MC<11600
import net.minecraft.client.gui.GuiConfirmOpenLink
//#else
//$$ import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen
//#endif


class ConfirmLinkScreen(private val url: String, private val previousScreen: GuiScreen?, trusted: Boolean = false):
    //#if MC<11600
    GuiConfirmOpenLink({ result, _ ->
    //#else
    //$$ ConfirmOpenLinkScreen({ result ->
    //#endif
        if (result) UDesktop.browse(url.toURI())
        UScreen.displayScreen(previousScreen)
    }, url,
        //#if MC<11600
        0,
        //#endif
        trusted
    ) {

    override fun
            //#if MC<11600
            keyTyped(typedChar: Char, keyCode: Int)
            //#else
            //$$ keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean
            //#endif
    {
        if (keyCode == UKeyboard.KEY_ESCAPE) {
            UScreen.displayScreen(previousScreen)
            //#if MC>=11600
            //$$ return true
            //#endif
        } else {
            //#if MC<11600
            super.keyTyped(typedChar, keyCode)
            //#else
            //$$ return super.keyPressed(keyCode, scanCode, modifiers)
            //#endif
        }
    }
}