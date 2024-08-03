package com.github.klee0kai.thekey.core.utils.common

import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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

fun SafeContextScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    trackFlow: MutableStateFlow<Int>? = null,
    mutex: Mutex? = null,
    @StringRes globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context = context, start = CoroutineStart.UNDISPATCHED) {
    mutex.withLockOrRun {
        yield() //set to context
        try {
            trackFlow?.update { it + 1 }
            GlobalJobsCollection.trackJob(globalRunDesc) { block() }
        } finally {
            trackFlow?.update { it - 1 }
        }
    }
}

fun SafeContextScope.launchSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    mutex: Mutex = this.mutex,
    trackFlow: MutableStateFlow<Int>? = null,
    @StringRes globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context = context, start = CoroutineStart.UNDISPATCHED) {
    mutex.withLock {
        yield() //set to context
        try {
            trackFlow?.update { it + 1 }
            GlobalJobsCollection.trackJob(globalRunDesc) { block() }
        } finally {
            trackFlow?.update { it - 1 }
        }
    }
}

fun <R> SafeContextScope.asyncSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    mutex: Mutex = this.mutex,
    trackFlow: MutableStateFlow<Int>? = null,
    @StringRes globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> R
) = async(context = context, start = CoroutineStart.UNDISPATCHED) {
    mutex.withLock {
        yield() //set to context
        try {
            trackFlow?.update { it + 1 }
            GlobalJobsCollection.trackJob(globalRunDesc) { block() }
        } finally {
            trackFlow?.update { it - 1 }
        }
    }
}

fun <R> SafeContextScope.asyncResult(
    context: CoroutineContext = EmptyCoroutineContext,
    mutex: Mutex? = null,
    trackFlow: MutableStateFlow<Int>? = null,
    @StringRes globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> R
) = async(context = context, start = CoroutineStart.UNDISPATCHED) {
    kotlin.runCatching {
        mutex.withLockOrRun {
            yield() //set to context
            try {
                trackFlow?.update { it + 1 }
                GlobalJobsCollection.trackJob(globalRunDesc) { block() }
            } finally {
                trackFlow?.update { it - 1 }
            }
        }
    }
}

fun <R> SafeContextScope.asyncResultSafe(
    context: CoroutineContext = EmptyCoroutineContext,
    mutex: Mutex = this.mutex,
    trackFlow: MutableStateFlow<Int>? = null,
    @StringRes globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> R
) = async(context = context, start = CoroutineStart.UNDISPATCHED) {
    kotlin.runCatching {
        mutex.withLockOrRun {
            yield() //set to context
            try {
                trackFlow?.update { it + 1 }
                GlobalJobsCollection.trackJob(globalRunDesc) { block() }
            } finally {
                trackFlow?.update { it - 1 }
            }
        }
    }
}


fun SafeContextScope.launchLatest(
    key: String,
    context: CoroutineContext = EmptyCoroutineContext,
    @StringRes globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(context = context) {
    GlobalJobsCollection.trackJob(globalRunDesc) { block() }
}.also { curJob ->
    singleRunJobs[key]?.cancel()
    singleRunJobs[key] = curJob;
}

fun SafeContextScope.launchIfNotStarted(
    key: String,
    context: CoroutineContext = EmptyCoroutineContext,
    @StringRes globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val latest = singleRunJobs[key]
    if (latest?.isActive == true) return latest
    latest?.cancel()

    return launch(context = context) {
        GlobalJobsCollection.trackJob(globalRunDesc) { block() }
    }.also { curJob ->
        singleRunJobs[key] = curJob;
    }
}


fun SafeContextScope.launchLatestSafe(
    key: String,
    context: CoroutineContext = EmptyCoroutineContext,
    @StringRes globalRunDesc: Int = 0,
    block: suspend CoroutineScope.() -> Unit
): Job = launch(
    context = context,
    start = CoroutineStart.UNDISPATCHED,
) {
    mutex.withLock {
        yield() //set to context
        GlobalJobsCollection.trackJob(globalRunDesc) { block() }
    }
}.also { curJob ->
    singleRunJobs[key]?.cancel()
    singleRunJobs[key] = curJob;
}

suspend inline fun <T> Mutex?.withLockOrRun(action: () -> T): T {
    return if (this != null) {
        withLock(action = action)
    } else {
        action()
    }
}