@file:OptIn(ExperimentalStdlibApi::class)

package com.github.klee0kai.thekey.core.utils.file

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

val File.parents get() = generateSequence(this) { it.parentFile }

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

fun String.removeFileExtension(): String {
    val folderIndex = lastIndexOf(File.separator)
    val index = indexOf(".", startIndex = folderIndex)
    if (index <= 0) return this
    return substring(0, index)
}

fun File.createNewWithSuffix(): File {
    var index = 1
    var newFile = this
    while (newFile.exists()) {
        newFile = File("${parent ?: ""}/${nameWithoutExtension}-${index++}.${extension}")
    }
    return newFile
}

fun File.sha256Checksum(): String? {
    if (!exists()) return null
    val buffSize = 1024
    val digest = MessageDigest.getInstance("SHA-256")
    val buffer = ByteArray(buffSize)
    FileInputStream(this).use { fis ->
        var read = fis.read(buffer, 0, buffSize)
        while (read > -1) {
            digest.update(buffer, 0, read)
            read = fis.read(buffer, 0, buffSize)
        }
    }
    return buffer.toHexString()
}