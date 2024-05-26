package com.github.klee0kai.thekey.core.utils.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

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

suspend inline fun <reified T> Deferred<T>.awaitSec(): T? = withTimeoutOrNull(1000) {
    await()
}