/*
 * This file is part of Resourcify
 * Copyright (C) 2026 DeDiamondPro
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

package dev.dediamondpro.resourcify.util.image

import java.awt.image.BufferedImage

/**
 * Function to try to infer if an image is pixel art, used to chose scaling option
 */
fun BufferedImage.isPixelArt(): Boolean {
    // We will treat images smaller than 64x64 as pixel art
    if (this.width <= 64 && this.height <= 64) {
        return true
    }

    // Choose the grid size by looping over some primes and checking if the dimensions of the image match
    val possibleGridSizes = arrayOf(2, 3, 5, 7, 11, 13, 17, 19)
    val gridSize: Int = possibleGridSizes.firstOrNull { this.width % it == 0 && this.height % it == 0 } ?: return false

    // Now verify the grid size by checking color doesn't change in the grid size
    // We only check 10 lines for performance and this is usually plenty
    val linesToVisit = 10
    for (i in 0 until linesToVisit) {
        val x = (this.width / linesToVisit) * i

        for (gridY in 0 until this.height / gridSize) {
            val gridYBase = gridY * gridSize
            val gridColor = this.getRGB(x, gridYBase)
            for (deltaY in 1 until gridSize)  {
                // Check the color matches the base color for the entire grid
                if (gridColor != this.getRGB(x, gridYBase + deltaY)) {
                    return false
                }
            }
        }
    }
    return true
}