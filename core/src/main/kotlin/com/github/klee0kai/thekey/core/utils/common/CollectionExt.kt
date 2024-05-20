package com.github.klee0kai.thekey.core.utils.common

inline fun <T> Iterable<T>.runForEach(action: T.() -> Unit) =
    forEach { action.invoke(it) }