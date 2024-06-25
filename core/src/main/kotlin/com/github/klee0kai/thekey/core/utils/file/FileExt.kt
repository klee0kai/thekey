package com.github.klee0kai.thekey.core.utils.file

import java.io.File

fun File.createNewWithSuffix(): File {
    var index = 1
    var newFile = this
    while (newFile.exists()) {
        newFile = File("${parent ?: ""}/${nameWithoutExtension}-${index++}.${extension}")
    }
    return newFile
}