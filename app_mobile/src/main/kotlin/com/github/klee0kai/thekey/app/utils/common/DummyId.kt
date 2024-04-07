package com.github.klee0kai.thekey.app.utils.common

object DummyId {

    private var dummyIdCounter = 0L

    val dummyId get() = dummyIdCounter++

}