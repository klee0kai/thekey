package com.github.klee0kai.thekey.core.utils.common

import java.util.UUID

object Dummy {

    private var dummyIdCounter = 0L

    val dummyId get() = dummyIdCounter++

    val unicString get() = UUID.randomUUID().toString()

}