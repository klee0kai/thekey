package com.github.klee0kai.thekey.app.utils.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> singleEventFlow(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T
) = channelFlow {
    val result = block()
    send(result)
}.flowOn(coroutineContext)
