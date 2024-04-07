package com.github.klee0kai.thekey.app.utils.lazymodel

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Stable
interface LazyModel<T, R> {
    companion object {
        internal val notLoaded = object {};
    }

    val isLoaded: Boolean

    val placeholder: T

    val fullValueFlow: Flow<R>

    fun getOrNull(): R?

    fun dirty()

}


suspend fun <T, R> LazyModel<T, R>.fullValue() = fullValueFlow.first()
