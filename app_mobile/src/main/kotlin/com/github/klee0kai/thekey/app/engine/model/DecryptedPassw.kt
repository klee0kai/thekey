package com.github.klee0kai.thekey.app.engine.model

import com.github.klee0kai.brooklyn.JniPojo

@JniPojo
data class DecryptedPassw(
    val passw: String = "",
    val chTime: Long = 0,
)
