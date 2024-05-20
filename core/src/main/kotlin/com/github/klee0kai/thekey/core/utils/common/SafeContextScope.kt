package com.github.klee0kai.thekey.core.utils.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class SafeContextScope(
    override val coroutineContext: CoroutineContext,
    val mutex: Mutex = Mutex(),
) : CoroutineScope {
    internal val singleRunJobs = ConcurrentHashMap<String, Job>()
}

fun SafeContextScope.launchSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context = context, start = CoroutineStart.UNDISPATCHED) {
    mutex.withLock {
        yield() //set to context
        block()
    }
}

fun <R> SafeContextScope.asyncSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> R
) = async(context = context, start = CoroutineStart.UNDISPATCHED) {
    mutex.withLock {
        yield() //set to context
        block()
    }
}

fun SafeContextScope.launchLatest(
    key: String,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context = context) {
    block()
}.also { curJob ->
    singleRunJobs[key]?.cancel()
    singleRunJobs[key] = curJob;
}

fun SafeContextScope.launchLatestSafe(
    key: String,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context = context, start = CoroutineStart.UNDISPATCHED) {
    mutex.withLock {
        yield() //set to context
        block()
    }
}.also { curJob ->
    singleRunJobs[key]?.cancel()
    singleRunJobs[key] = curJob;
}