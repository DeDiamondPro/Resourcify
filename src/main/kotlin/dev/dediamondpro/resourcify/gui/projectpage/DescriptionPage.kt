/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.modrinth.ProjectResponse
import dev.dediamondpro.resourcify.modrinth.Version
import dev.dediamondpro.resourcify.util.DummyCache
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.markdown.MarkdownComponent
import java.awt.Color

class DescriptionPage(
    project: ProjectResponse, versions: List<Version>? = null, hashes: List<String>? = null
) : UIBlock(color = Color(0, 0, 0, 100)) {
    init {
        constrain {
            x = 0.pixels(alignOpposite = true)
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildLocationSizeConstraint() + 8.pixels()
        }
        MarkdownComponent(project.body, disableSelection = true, imageCache = DummyCache).constrain {
            x = 8.pixels()
            y = 8.pixels()
            width = 100.percent() - 16.pixels()
        } childOf this
    }
}