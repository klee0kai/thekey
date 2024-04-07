package com.github.klee0kai.thekey.app.utils.lazymodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModel.Companion.notLoaded
import com.github.klee0kai.thekey.app.utils.views.TargetAlpha
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


@Stable
class LazyModelProvider<T, R>(
    override val placeholder: T,
    preloaded: R? = null,
    private val lazyProvide: suspend LazyModelProvider<T, R>.() -> R,
) : LazyModel<T, R> {
    companion object;

    private var dirty = MutableStateFlow(false)
    private var fullValueLoaded: Any? = notLoaded
    private val mutex = Mutex()

    override val isLoaded get() = !dirty.value && fullValueLoaded != notLoaded

    override val fullValueFlow = channelFlow<R> {
        while (isActive) {
            mutex.withLock {
                if (dirty.value || fullValueLoaded == notLoaded) {
                    fullValueLoaded = lazyProvide()
                    dirty.value = false
                }
            }
            send(fullValueLoaded as R)
            dirty.first { it }
        }
    }

    init {
        if (preloaded != null) {
            fullValueLoaded = preloaded
        }
    }

    override fun getOrNull() = fullValueLoaded.takeIf { it != notLoaded } as? R

    override fun dirty() {
        dirty.value = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LazyModel<*, *>
        return placeholder == other.placeholder
    }

    override fun hashCode(): Int {
        return placeholder?.hashCode() ?: 0
    }
}

fun <T, R> fromPreloadedOrCreate(placeholder: T, old: List<LazyModel<T, R>>, lazyProvide: suspend LazyModel<T, R>.() -> R): LazyModel<T, R> {
    return old.firstOrNull { it.placeholder == placeholder } ?: LazyModelProvider(placeholder, lazyProvide = lazyProvide)
}


@Composable
fun <T, R> LazyModel<T, R>.collectAsStateCrossFaded(): State<TargetAlpha<R?>> =
    fullValueFlow.collectAsStateCrossFaded(key = placeholder, initial = getOrNull())