package com.github.klee0kai.thekey.core.helpers.path

const val tKeyFormat = ".ckey"
const val tKeyExtension = "ckey"

fun String.appendTKeyFormat(): String {
    val path = this
    if (path.isBlank()) return path;
    if (path.endsWith(tKeyFormat)) {
        return path
    }
    return path + tKeyFormat
}

fun String.removeTKeyFormat(): String {
    val path = this
    if (path.isBlank()) return path;
    if (path.endsWith(tKeyFormat)) {
        return path.substring(0, path.length - tKeyFormat.length)
    }
    return path;
}
