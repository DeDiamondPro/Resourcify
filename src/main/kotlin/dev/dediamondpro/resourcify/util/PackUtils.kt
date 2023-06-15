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

package dev.dediamondpro.resourcify.util

import java.io.File

object PackUtils {

    fun getPackHashes(directory: File): List<String> {
        return getPackFiles(directory).mapNotNull { Utils.getSha512(it) }
    }

    private fun getPackFiles(directory: File): List<File> {
        val files = directory.listFiles() ?: return emptyList()
        val packs = files.filter { it.isFile && it.extension == "zip"  }.toMutableList()
        files.filter { it.isDirectory }.forEach { packs.addAll(getPackFiles(it)) }
        return packs
    }
}