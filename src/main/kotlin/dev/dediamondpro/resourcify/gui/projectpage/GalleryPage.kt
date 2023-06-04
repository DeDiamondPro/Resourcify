/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.resourcify.constraints.ChildLocationSizeConstraint
import dev.dediamondpro.resourcify.gui.projectpage.components.GalleryCard
import dev.dediamondpro.resourcify.modrinth.ProjectResponse
import dev.dediamondpro.resourcify.modrinth.Version
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import kotlin.math.ceil

class GalleryPage(project: ProjectResponse, versions: List<Version>, hashes: List<String>) : UIContainer() {
    init {
        constrain {
            x = 0.pixels(alignOpposite = true)
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildLocationSizeConstraint()
        }

        val gallery = project.gallery.sortedBy { it.ordering }
        for (i in 0 until ceil(gallery.size / 2f).toInt()) {
            val row = UIContainer().constrain {
                x = 0.pixels()
                y = SiblingConstraint(padding = 4f)
                width = 100.percent()
                height = ChildBasedMaxSizeConstraint()
            } childOf this
            GalleryCard(gallery[i * 2]).constrain {
                x = 0.pixels()
                y = 0.pixels()
                width = 50.percent() - 2.pixels()
            } childOf row
            if (gallery.size > i * 2 + 1) GalleryCard(gallery[i * 2 + 1]).constrain {
                x = 0.pixels(true)
                y = 0.pixels()
                width = 50.percent() - 2.pixels()
            } childOf row
        }
    }
}