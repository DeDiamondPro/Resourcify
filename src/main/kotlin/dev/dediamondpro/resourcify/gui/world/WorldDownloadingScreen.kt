package dev.dediamondpro.resourcify.gui.world

import dev.dediamondpro.resourcify.Constants
import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.util.DownloadManager
import dev.dediamondpro.resourcify.util.runAsync
import gg.essential.universal.UScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.DisconnectedScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.TitleScreen
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen
import net.minecraft.network.chat.Component
import java.io.File
import java.net.URI
import kotlin.math.round


//? if >=1.20.5 {
import net.minecraft.client.gui.screens.GenericMessageScreen
//?} else
/*import net.minecraft.client.gui.screens.GenericDirtMessageScreen*/

class WorldDownloadingScreen(val parent: PaginatedScreen, val world: File, val uri: URI) :
/*? if >=1.20.5 {*/ GenericMessageScreen /*?} else {*/ /*Screen *//*?}*/
    (Component.translatable("resourcify.world.downloading", 0)) {
    private var cancelButton: Button? = null

    //? if <1.20.5
    /*private var textWidget: Component = Component.translatable("resourcify.world.downloading", 0)*/
    private var triggeredLoad = false

    override fun init() {
        super.init()
        val width = getTextWidth()
        cancelButton = this.addRenderableWidget(
            Button.builder(Component.translatable("resourcify.world.cancel")) {
                DownloadManager.cancelDownload(uri)
                UScreen.displayScreen(parent)
            }
                .width(width + 24)
                .pos(this.width / 2 - width / 2 - 12, this.height / 2 + 20)
                .build()
        )
    }

    override fun repositionElements() {
        super.repositionElements()
        val width = getTextWidth()
        cancelButton?.width = width + 24
        cancelButton?.x = this.width / 2 - width / 2 - 12
        cancelButton?.y = this.height / 2 + 20
    }

    override fun tick() {
        super.tick()
        val progress = DownloadManager.getProgress(uri)
        if (progress != null) {
            updateText(Component.translatable("resourcify.world.downloading", round(progress * 100.0).toInt()))
            this.repositionElements()
            return
        }

        if (triggeredLoad) {
            return
        }
        triggeredLoad = true

        // We are done downloading, open the world
        openWorld(world.name)
    }

    private fun updateText(newText: Component) {
        //? if >=1.20.5 {
        this.textWidget?.message = newText
        //?} else
        /*this.textWidget = newText*/
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        //? if >=1.20.5 {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        //?} else {
        /*this.renderDirtBackground(guiGraphics)
        guiGraphics.drawCenteredString(this.font, this.textWidget, this.width / 2, this.height / 2, 16777215)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        *///?}
    }

    private fun getTextWidth(): Int {
        //? if >=1.20.5 {
        return this.textWidget!!.width
        //?} else
        /*return this.font.width(this.textWidget)*/
    }

    override fun shouldCloseOnEsc(): Boolean {
        return false
    }

    companion object {
        fun openWorld(name: String) {
            val minecraft = Minecraft.getInstance()
            if (!minecraft.levelSource.levelExists(name)) {
                Constants.LOGGER.error("Failed to open world $name, it doesn't exist")
                UScreen.displayScreen(createFailScreen(name))
            } else {
                // This is a stupid way of doing it, but it's the only way I can find that doesn't trigger
                // Fabric API's post screen tick event's null check (thanks fabric!)
                // this is basically the same issues as here: https://github.com/FabricMC/fabric/issues/1289
                runAsync {
                    minecraft.execute {
                        //? if >=1.21 {
                        minecraft.createWorldOpenFlows().openWorld(name) {
                            Constants.LOGGER.error("Failed to open world $name")
                            UScreen.displayScreen(createFailScreen(name))
                        }
                        //?} else {
                        /*minecraft!!.forceSetScreen(GenericDirtMessageScreen(Component.translatable("selectWorld.data_read")))
                        minecraft!!.createWorldOpenFlows().loadLevel(createFailScreen(name), name)
                        *///?}
                    }
                }
            }
        }

        private fun createFailScreen(name: String): Screen {
            return DisconnectedScreen(
                SelectWorldScreen(TitleScreen()),
                Component.translatable("resourcify.world.failed"),
                Component.translatable("resourcify.world.failed.description", name),
                Component.translatable("gui.toWorld")
            )
        }
    }
}