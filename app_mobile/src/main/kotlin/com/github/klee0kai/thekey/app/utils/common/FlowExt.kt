package com.github.klee0kai.thekey.app.utils.common

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> singleEventFlow(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    block: suspend () -> T
) = flow {
    val result = block()
    emit(result)
}.flowOn(coroutineContext)
