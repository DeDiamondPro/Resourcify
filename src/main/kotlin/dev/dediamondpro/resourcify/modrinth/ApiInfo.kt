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

object ApiInfo {
    const val API = "https://api.modrinth.com/v2"

    enum class ProjectType(
        val displayName: String,
        val projectType: String,
        val searchFacet: String,
        val loader: String,
        val plusX: XConstraint = CenterConstraint() + 194.pixels(),
        val plusY: YConstraint = 10.pixels()
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
            "datapack"
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
        )
    }
}