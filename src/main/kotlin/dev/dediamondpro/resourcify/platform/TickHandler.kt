/*
 * This file is part of Resourcify
 * Copyright (C) 2025 DeDiamondPro
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

package dev.dediamondpro.resourcify.platform

object TickHandler {
    private val listeners = mutableListOf<() -> Unit>()
    private val temporaryListeners = mutableListOf<() -> Unit>()

    fun registerListener(runnable: () -> Unit) {
        listeners.add(runnable)
    }

    fun runAtNextTick(runnable: () -> Unit) {
        temporaryListeners.add(runnable)
    }

    fun onTickStart() {
        listeners.forEach { it() }

        // Create copy so if temp listener is added here it won't be run until next tick
        val tempCopy = temporaryListeners.toList()
        temporaryListeners.clear()
        tempCopy.forEach { it() }
    }
}