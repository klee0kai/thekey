package com.github.klee0kai.thekey.app.model

import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.utils.common.LazyModel
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias LazyPassw = LazyModel<Long, DecryptedPassw>

val LazyPassw.id get() = placeholder

fun dummyLazyPassw() = LazyPassw(1) {
    delay(Random.nextLong(3000))
    DecryptedPassw(passw = "passw")
}
