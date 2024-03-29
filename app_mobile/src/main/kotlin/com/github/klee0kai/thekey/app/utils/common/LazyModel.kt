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
    val value: T,
    private val lazyProvide: suspend () -> R,
) {
    companion object;

    internal var preloaded: Any? = notLoaded
    internal var fullValueLoaded: Any? = notLoaded
    internal val mutex = Mutex()

    val isLoaded get() = fullValueLoaded != notLoaded

    fun fullValueFlow() = singleEventFlow<R> {
        if (fullValueLoaded == notLoaded) {
            fullValueLoaded = lazyProvide.invoke()
        }
        fullValueLoaded as R
    }

    suspend fun fullValue() = fullValueFlow().first()

    fun getOrNull() = fullValueLoaded.takeIf { it != notLoaded } as? R
        ?: preloaded.takeIf { it != notLoaded } as? R

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
        note.preloaded = cachedMap[note.value]
            ?.firstOrNull()
            ?.getOrNull()
            ?: notLoaded
    }
}


@Composable
fun <T, R> LazyModel<T, R>.collectAsStateCrossFaded(
    context: CoroutineContext = EmptyCoroutineContext
): State<TargetAlpha<R?>> = fullValueFlow().collectAsStateCrossFaded(key = value, initial = getOrNull())