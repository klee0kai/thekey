package com.github.klee0kai.thekey.feature.qrcodescanner.utils

import com.github.klee0kai.thekey.app.di.DI
import com.google.android.gms.tasks.Task
import com.google.common.util.concurrent.ListenableFuture
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

suspend fun <T> ListenableFuture<T>.await(): T = suspendCoroutine { cont ->
    addListener(Runnable {
        cont.resume(get())
    }, DI.defaultExecutor())
}
