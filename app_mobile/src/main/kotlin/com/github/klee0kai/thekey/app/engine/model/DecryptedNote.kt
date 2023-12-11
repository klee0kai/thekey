package com.github.klee0kai.thekey.app.engine.model

import com.github.klee0kai.brooklyn.JniPojo

@JniPojo
data class DecryptedNote(
    val ptnote: Long = 0L,
    val site: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",

    val chTime: Long = 0,
    val hist: Array<DecryptedPassw> = emptyArray(),
)
