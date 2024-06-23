package com.github.klee0kai.thekey.core.utils.coroutine

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun <T> CoroutineScope.asyncResult(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
) = async(context, start) {
    runCatching {
        block()
    }
}

fun emptyJob(): Job = Job().also { it.complete() }

fun <T> completeAsync(result: T): Deferred<T> = CompletableDeferred(value = result)

suspend fun <T> minDuration(duration: Duration, block: suspend () -> T): T {
    val start = System.currentTimeMillis()
    val r = block.invoke()
    val left = duration - (System.currentTimeMillis() - start).milliseconds
    if (left.isPositive()) delay(left)
    return r
}

suspend inline fun <reified T> Deferred<T>.awaitSec(): T? = withTimeoutOrNull(1000) {
    await()
}