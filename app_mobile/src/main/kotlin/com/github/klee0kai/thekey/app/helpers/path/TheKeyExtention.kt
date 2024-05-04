package com.github.klee0kai.thekey.app.helpers.path

const val tKeyFormat = ".ckey"

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
