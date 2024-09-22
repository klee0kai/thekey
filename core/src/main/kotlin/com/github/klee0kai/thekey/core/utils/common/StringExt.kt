package com.github.klee0kai.thekey.core.utils.common

fun String.appendSuffix(suffix: String): String {
    return if (endsWith(suffix)) {
        this
    } else {
        this + suffix
    }
}