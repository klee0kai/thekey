package com.github.klee0kai.thekey.core.utils.file

import java.io.InputStream
import java.io.OutputStream

private const val BUFFER_SIZE = 512

fun InputStream.writeAndClose(outputStream: OutputStream?) {
    try {
        val bytes = ByteArray(BUFFER_SIZE)
        while (true) {
            val readCount = read(bytes)
            if (readCount <= 0) break
            outputStream?.write(bytes, 0, readCount)
        }
    } finally {
        runCatching { close() }
        runCatching { outputStream?.close() }
    }
}

fun InputStream.writeTo(outputStream: OutputStream) {
    val bytes = ByteArray(BUFFER_SIZE)
    while (true) {
        val readCount = read(bytes)
        if (readCount <= 0) break
        outputStream.write(bytes, 0, readCount)
    }
}

