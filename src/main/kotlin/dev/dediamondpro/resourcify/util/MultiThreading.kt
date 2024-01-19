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
