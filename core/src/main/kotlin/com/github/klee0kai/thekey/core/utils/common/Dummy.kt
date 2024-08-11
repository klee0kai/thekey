package com.github.klee0kai.thekey.core.utils.common

import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

object Dummy {

    private val dummyIdCounter = AtomicLong()

    val dummyId get() = dummyIdCounter.incrementAndGet()

    val unicString get() = UUID.randomUUID().toString()

}