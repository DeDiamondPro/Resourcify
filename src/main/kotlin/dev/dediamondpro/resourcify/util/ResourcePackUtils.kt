/*
 * Copyright (C) 2023 DeDiamondPro. - All Rights Reserved
 */

package dev.dediamondpro.resourcify.util

import dev.dediamondpro.resourcify.platform.Platform
import java.io.File

object ResourcePackUtils {

    fun getPackHashes(): List<String> {
        return getPackFiles().mapNotNull { Utils.getSha1(it) }
    }

    fun getPackFiles(directory: File = Platform.getResourcePackDirectory()): List<File> {
        val files = directory.listFiles() ?: return emptyList()
        val packs = files.filter { it.isFile && it.extension == "zip"  }.toMutableList()
        files.filter { it.isDirectory }.forEach { packs.addAll(getPackFiles(it)) }
        return packs
    }
}