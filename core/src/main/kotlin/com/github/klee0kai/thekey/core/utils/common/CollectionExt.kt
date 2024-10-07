package com.github.klee0kai.thekey.core.utils.common

inline fun <reified T> buildListCount(count: Int, action: () -> T): List<T> {
    val list = mutableListOf<T>()
    repeat(count) {
        list.add(action())
    }
    return list
}

inline fun <T> Iterable<T>.runForEach(action: T.() -> Unit) =
    forEach { action.invoke(it) }

inline fun <reified T> Iterable<T>.accumulate(action: (T, T) -> T): T? {
    var last: T? = null
    forEachIndexed { index, t ->
        last = if (index > 0) {
            action(last as T, t)
        } else {
            t
        }
    }
    return last
}
