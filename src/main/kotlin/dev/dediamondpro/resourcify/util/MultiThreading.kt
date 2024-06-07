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

package dev.dediamondpro.resourcify.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.Supplier

/**
 * Helper class to help with multithreading, required to make sure the classloader is correct on modern forge
 */
object MultiThreading {
    private val pool = Executors.newCachedThreadPool(this::createThread)

    fun <U> supplyAsync(supplier: Supplier<U>): CompletableFuture<U> {
        return CompletableFuture.supplyAsync(supplier, pool)
    }

    fun runAsync(runnable: Runnable): CompletableFuture<Void> {
        return CompletableFuture.runAsync(runnable, pool)
    }

    private fun createThread(r: Runnable): Thread {
        val thread = Executors.defaultThreadFactory().newThread(r)
        thread.contextClassLoader = Thread.currentThread().contextClassLoader
        return thread
    }
}

fun <U> supplyAsync(supplier: Supplier<U>): CompletableFuture<U> = MultiThreading.supplyAsync(supplier)

fun runAsync(runnable: Runnable): CompletableFuture<Void> = MultiThreading.runAsync(runnable)

fun <U> supply(supplier: Supplier<U>): CompletableFuture<U> = CompletableFuture.completedFuture(supplier.get())