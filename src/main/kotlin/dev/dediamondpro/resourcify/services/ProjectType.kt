/*
 * This file is part of Resourcify
 * Copyright (C) 2024 DeDiamondPro
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

package dev.dediamondpro.resourcify.services

//#if MC >= 11600
//$$ import dev.dediamondpro.resourcify.mixins.PackScreenAccessor
//$$ import net.minecraft.client.gui.screen.Screen
//$$ import java.io.File
//#endif

enum class ProjectType(
    val displayName: String,
    val plusX: (Int) -> Int = { screenWidth: Int ->
        (
                screenWidth / 2
                //#if MC >= 12005
                //$$ + 195
                //#else
                + 184
                //#endif
        )
    },
    val plusY: (Int) -> Int = { 10 },
    val hasUpdateButton: Boolean = true
) {
    RESOURCE_PACK("resourcify.type.resource_packs"),
    // 1.8.9 only
    AYCY_RESOURCE_PACK("resourcify.type.resource_packs", plusX = { it - 30 }),
    DATA_PACK("resourcify.type.data_packs", hasUpdateButton = false),
    IRIS_SHADER("resourcify.type.shaders",  { it / 2 + 134 }, { 6 }),
    OPTIFINE_SHADER("resourcify.type.shaders", plusX = { it - 30 });

    //#if MC >= 11600
    //$$ fun getDirectory(screen: Screen): File {
    //$$     return when(this) {
    //$$         //#if MC < 11904
    //$$         RESOURCE_PACK -> (screen as PackScreenAccessor).directory
    //$$         DATA_PACK -> (screen as PackScreenAccessor).directory
    //$$         //#else
    //$$         //$$ RESOURCE_PACK -> (screen as PackScreenAccessor).directory.toFile()
    //$$         //$$ DATA_PACK -> (screen as PackScreenAccessor).directory.toFile()
    //$$         //#endif
    //$$         IRIS_SHADER -> File("./shaderpacks")
    //$$         OPTIFINE_SHADER -> File("./shaderpacks")
    //$$         else -> error("Unknown project type: $this")
    //$$     }
    //$$ }
    //#endif
}