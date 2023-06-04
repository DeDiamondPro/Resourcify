/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.gui.projectpage

import dev.dediamondpro.resourcify.gui.projectpage.components.VersionCard
import dev.dediamondpro.resourcify.modrinth.ProjectResponse
import dev.dediamondpro.resourcify.modrinth.Version
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels

class VersionsPage(project: ProjectResponse, versions: List<Version>, hashes: List<String>) : UIContainer() {
    init {
        constrain {
            x = 0.pixels(alignOpposite = true)
            y = SiblingConstraint(padding = 4f)
            width = 100.percent()
            height = ChildBasedSizeConstraint()
        }
        for (version in versions) {
            VersionCard(version, hashes).constrain {
                x = 0.pixels()
                y = SiblingConstraint(padding = 4f)
            } childOf this
        }
    }
}