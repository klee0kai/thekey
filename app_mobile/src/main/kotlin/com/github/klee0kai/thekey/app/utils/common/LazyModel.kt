package com.github.klee0kai.thekey.app.utils.common

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val notLoaded = object {};

class LazyModel<T, R>(
    val value: T,
    private val lazyProvide: suspend () -> R,
) {
    companion object;

    private var fullValueLoaded: Any? = notLoaded
    private val mutex = Mutex()

    suspend fun fullValue(): R = mutex.withLock {
        if (fullValueLoaded == notLoaded) {
            fullValueLoaded = lazyProvide.invoke()
        }
        fullValueLoaded as R
    }

}