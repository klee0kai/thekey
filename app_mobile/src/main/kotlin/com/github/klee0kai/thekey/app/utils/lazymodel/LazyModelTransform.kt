package com.github.klee0kai.thekey.app.utils.lazymodel

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Stable
@Deprecated("difficult to use")
class LazyModelTransform<T, R1, R2>(
    val origin: LazyModel<T, R1>,
    val transform: suspend (R1) -> R2
) : LazyModel<T, R2> {

    override val placeholder: T get() = origin.placeholder
    override val isLoaded get() = origin.isLoaded

    private var fullValueLoaded: Any? = LazyModel.notLoaded


    override val fullValueFlow = origin.fullValueFlow
        .map(transform)
        .onEach { fullValueLoaded = it }

    override fun getOrNull() =
        fullValueLoaded.takeIf { it != LazyModel.notLoaded } as? R2

    override fun dirty() = origin.dirty()

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

fun <T, R1, R2> LazyModel<T, R1>.map(transform: suspend (R1) -> R2) =
    LazyModelTransform(
        origin = this,
        transform = transform
    )