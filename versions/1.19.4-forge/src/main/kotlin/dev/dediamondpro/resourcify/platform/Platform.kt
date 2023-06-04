/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.platform

import gg.essential.universal.UMinecraft
import net.minecraft.SharedConstants
import java.io.File

object Platform {
    fun getMcVersion(): String {
       return SharedConstants.getCurrentVersion().name
    }

    fun getResourcePackDirectory(): File {
        return UMinecraft.getMinecraft().resourcePackDirectory.toFile()
    }
}