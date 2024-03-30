package com.github.klee0kai.thekey.app.utils.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import com.github.klee0kai.thekey.app.utils.views.TargetAlpha
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private val notLoaded = object {};

@Stable
class LazyModel<T, R>(
    val placeholder: T,
    private val lazyProvide: suspend LazyModel<T, R>.() -> R,
) {
    companion object;

    var dirty: Boolean = true
    internal var fullValueLoaded: Any? = notLoaded
    internal val mutex = Mutex()

    val isLoaded get() = !dirty && fullValueLoaded != notLoaded

    fun fullValueFlow() = singleEventFlow<R> {
        if (dirty || fullValueLoaded == notLoaded) {
            fullValueLoaded = lazyProvide()
            dirty = false
        }
        fullValueLoaded as R
    }

    suspend fun fullValue() = fullValueFlow().first()

    fun getOrNull() = fullValueLoaded.takeIf { it != notLoaded } as? R

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
    return old.firstOrNull { it.placeholder == placeholder } ?: LazyModel(placeholder, lazyProvide)
}


@Composable
fun <T, R> LazyModel<T, R>.collectAsStateCrossFaded(
    context: CoroutineContext = EmptyCoroutineContext
): State<TargetAlpha<R?>> = fullValueFlow().collectAsStateCrossFaded(key = placeholder, initial = getOrNull())