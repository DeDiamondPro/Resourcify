/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.platform

import gg.essential.universal.UMinecraft
import net.minecraftforge.common.ForgeVersion
import java.io.File

object Platform {
    fun getMcVersion(): String {
       return ForgeVersion.mcVersion
    }

    fun getResourcePackDirectory(): File {
        return UMinecraft.getMinecraft().resourcePackRepository.dirResourcepacks
    }
}