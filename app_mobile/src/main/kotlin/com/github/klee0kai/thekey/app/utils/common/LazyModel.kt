package com.github.klee0kai.thekey.app.utils.common

import androidx.compose.runtime.Stable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val notLoaded = object {};

@Stable
class LazyModel<T, R>(
    val value: T,
    private val lazyProvide: suspend () -> R,
) {
    companion object;

    internal var fullValueLoaded: Any? = notLoaded
    internal val mutex = Mutex()

    val isLoaded get() = fullValueLoaded != notLoaded

    suspend fun fullValue(): R = mutex.withLock {
        if (fullValueLoaded == notLoaded) {
            fullValueLoaded = lazyProvide.invoke()
        }
        fullValueLoaded as R
    }

    fun fullValueFlow() = singleEventFlow<R> {
        if (fullValueLoaded == notLoaded) {
            fullValueLoaded = lazyProvide.invoke()
        }
        fullValueLoaded as R
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LazyModel<*, *>
        return value == other.value
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}


fun <T, R> List<LazyModel<T, R>>.preloaded(old: List<LazyModel<T, R>>) = apply {
    val cachedMap = old.groupBy { it.value }
    forEach { note ->
        note.fullValueLoaded = cachedMap[note.value]
            ?.firstOrNull()
            ?.fullValueLoaded
            ?: notLoaded
    }
}