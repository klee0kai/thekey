package com.github.klee0kai.thekey.app.utils.common

import kotlinx.coroutines.flow.flow

fun <T> singleEventFlow(
    block: suspend () -> T
) = flow {
    val result = block()
    emit(result)
}
