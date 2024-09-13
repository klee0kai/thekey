package com.github.klee0kai.thekey.core.domain.model

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object DebugConfigs {

    /**
     * modify data before it is actually saved in encrypted storage
     */
    var isNotesFastUpdate = true

    /**
     * engine long operations
     */
    var engineDelay: EngineLong = EngineLong.Fast

}

enum class EngineLong(
    val readDelay: Duration = Duration.ZERO,
    val writeDelay: Duration = Duration.ZERO,
) {
    Fast,
    LongWrite(writeDelay = 3.seconds),
    LongReadWrite(readDelay = 3.seconds, writeDelay = 3.seconds),
}

fun EngineLong.next(): EngineLong {
    val index = (EngineLong.entries.indexOf(this) + 1) % EngineLong.entries.size
    return EngineLong.entries[index]
}