package com.github.klee0kai.thekey.app.utils.coroutine

import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun <T> Task<T>.await(): T = suspendCoroutine { cont ->
    addOnCompleteListener {
        when {
            exception != null -> cont.resumeWithException(exception!!)
            else -> cont.resume(result)
        }
    }
}

suspend fun <T> com.google.android.play.core.tasks.Task<T>.await(): T = suspendCoroutine { cont ->
    addOnCompleteListener {
        when {
            exception != null -> cont.resumeWithException(exception!!)
            else -> cont.resume(result)
        }
    }
}
