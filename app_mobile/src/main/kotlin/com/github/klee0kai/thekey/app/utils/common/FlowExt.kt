package com.github.klee0kai.thekey.app.utils.common

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> singleEventFlow(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    block: suspend () -> T
) = flow {
    withContext(coroutineContext) {
        val result = block()
        emit(result)
    }
}
