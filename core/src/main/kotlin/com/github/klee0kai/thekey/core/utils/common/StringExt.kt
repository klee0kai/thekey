package com.github.klee0kai.thekey.core.utils.common

fun String.appendSuffix(suffix: String): String {
    return if (endsWith(suffix)) {
        this
    } else {
        this + suffix
    }
}

fun String.appendPrefix(prefix: String): String {
    return if (startsWith(prefix)) {
        this
    } else {
        prefix + this
    }
}