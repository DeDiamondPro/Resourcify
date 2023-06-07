/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.elements

import dev.dediamondpro.resourcify.gui.PaginatedScreen
import dev.dediamondpro.resourcify.util.Icons
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.basicColorConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UScreen
import net.minecraft.client.gui.GuiScreenResourcePacks
import java.awt.Color

class Paginator(screen: PaginatedScreen) : UIBlock(color = Color(0, 0, 0, 100)) {
    init {
        Icon(Icons.BACK, true).constrain {
            x = 8.pixels()
            y = CenterConstraint()
            width = 9.pixels()
            height = 9.pixels()
        }.onMouseClick { screen.goBack() } childOf this
        UIText("Close").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        }.onMouseClick {
            UScreen.displayScreen(PaginatedScreen.backScreens.findLast { it is GuiScreenResourcePacks })
            PaginatedScreen.cleanUp()
        } childOf this
        Icon(Icons.FORWARD, true, basicColorConstraint {
            if (PaginatedScreen.forwardScreens.isEmpty()) Color.LIGHT_GRAY else Color.WHITE
        }).constrain {
            x = 8.pixels(true)
            y = CenterConstraint()
            width = 9.pixels()
            height = 9.pixels()
        }.onMouseClick { screen.goForward() } childOf this
    }
}