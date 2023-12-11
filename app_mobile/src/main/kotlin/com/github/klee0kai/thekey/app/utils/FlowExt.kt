package com.github.klee0kai.thekey.app.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.takeWhile

inline fun <reified T> Flow<T>.shareLatest(scope: CoroutineScope): Flow<T> {
    val endl = object {}
    val orFlow = this
    val withEnd = flow {
        orFlow.collect { emit(it) }
        emit(endl)
    }
    return withEnd.shareIn(scope, SharingStarted.Lazily, 2)
        .takeWhile { it !== endl }
        .filterIsInstance<T>()
}