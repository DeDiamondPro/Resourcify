/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.platform

import gg.essential.universal.UMinecraft
import net.minecraft.util.SharedConstants
import java.io.File

object Platform {
    fun getMcVersion(): String {
       return SharedConstants.getVersion().name
    }

    fun getResourcePackDirectory(): File {
        return UMinecraft.getMinecraft().fileResourcePacks
    }
}