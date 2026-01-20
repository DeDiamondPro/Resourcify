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

package dev.dediamondpro.resourcify.util

import dev.dediamondpro.resourcify.elements.image.UIAnimatedImage
import gg.essential.elementa.components.UIImage
import java.lang.ref.WeakReference
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

abstract class Cache<T> {
    private val cache: MutableMap<String, WeakReference<T>> = ConcurrentHashMap()

    fun get(identifier: String): T? {
        val ref = cache[identifier] ?: return null
        val cached = ref.get()
        if (cached == null) {
            cache.remove(identifier)
        }
        return cached
    }

    fun get(identifier: URI): T? {
        return get(identifier.toString())
    }

    fun put(identifier: String, image: T) {
        cache[identifier] = WeakReference(image)
    }

    fun put(identifier: URI, image: T) {
        put(identifier.toString(), image)
    }

    fun getOrPut(identifier: String, imageSupplier: () -> T): T {
        return get(identifier) ?: imageSupplier().also { put(identifier, it) }
    }

    fun getOrPut(identifier: URI, imageSupplier: () -> T): T {
        return getOrPut(identifier.toString(), imageSupplier)
    }
}

object ImageCache : Cache<UIImage>();
object AnimatedImageCache : Cache<List<UIAnimatedImage.Frame>>()