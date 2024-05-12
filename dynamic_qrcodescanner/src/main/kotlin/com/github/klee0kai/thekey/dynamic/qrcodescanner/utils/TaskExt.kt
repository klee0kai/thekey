package com.github.klee0kai.thekey.dynamic.qrcodescanner.utils

import com.github.klee0kai.thekey.app.di.DI
import com.google.common.util.concurrent.ListenableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun <T> ListenableFuture<T>.await(): T = suspendCoroutine { cont ->
    addListener(Runnable {
        cont.resume(get())
    }, DI.defaultExecutor())
}
