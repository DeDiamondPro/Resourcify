/*
 * This file is part of Resourcify
 * Copyright (C) 2023 DeDiamondPro
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

package dev.dediamondpro.resourcify.modrinth

import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.XConstraint
import gg.essential.elementa.constraints.YConstraint
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus

//#if MC >= 11600
//$$ import net.minecraft.util.text.TranslationTextComponent
//$$ import dev.dediamondpro.resourcify.mixins.PackScreenAccessor
//$$ import net.minecraft.client.gui.screen.Screen
//$$ import java.io.File
//#endif

object ApiInfo {
    const val API = "https://api.modrinth.com/v2"

    enum class ProjectType(
        val displayName: String,
        val projectType: String,
        val searchFacet: String,
        val loader: String,
        val plusX: XConstraint = CenterConstraint() + 194.pixels(),
        val plusY: YConstraint = 10.pixels(),
        val hasUpdateButton: Boolean = true
    ) {
        RESOURCE_PACK(
            "resource packs",
            "resourcepack",
            "[\"project_type:resourcepack\"]",
            "minecraft"
        ),
        DATA_PACK(
            "data packs",
            "mod",
            "[\"project_type:mod\"],[\"categories=datapack\"]",
            "datapack",
            hasUpdateButton = false
        ),
        IRIS_SHADER(
            "shaders",
            "shader",
            "[\"project_type:shader\"],[\"categories=iris\"]",
            "iris",
            CenterConstraint() + 144.pixels(),
            6.pixels()
        ),
        OPTIFINE_SHADER(
            "shaders",
            "shader",
            "[\"project_type:shader\"],[\"categories=optifine\"]",
            "optifine",
            plusX = 10.pixels(true)
        );

        //#if MC >= 11600
        //$$ fun getDirectory(screen: Screen): File {
        //$$     return when(this) {
        //$$         //#if MC < 11904
        //$$         ApiInfo.ProjectType.RESOURCE_PACK -> (screen as PackScreenAccessor).directory
        //$$         ApiInfo.ProjectType.DATA_PACK -> (screen as PackScreenAccessor).directory
        //$$         //#else
        //$$         //$$ ApiInfo.ProjectType.RESOURCE_PACK -> (screen as PackScreenAccessor).directory.toFile()
        //$$         //$$ ApiInfo.ProjectType.DATA_PACK -> (screen as PackScreenAccessor).directory.toFile()
        //$$         //#endif
        //$$         ApiInfo.ProjectType.IRIS_SHADER -> File("./shaderpacks")
        //$$         ApiInfo.ProjectType.OPTIFINE_SHADER -> File("./shaderpacks")
        //$$     }
        //$$ }
        //#endif
    }
}