package com.github.klee0kai.thekey.app.utils.common

import java.util.UUID

object DummyId {

    private var dummyIdCounter = 0L

    val dummyId get() = dummyIdCounter++

    val unicString get() = UUID.randomUUID().toString()

}