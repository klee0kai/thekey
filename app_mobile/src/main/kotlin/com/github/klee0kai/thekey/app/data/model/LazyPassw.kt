package com.github.klee0kai.thekey.app.data.model

import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModel
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias LazyPassw = LazyModel<Long, DecryptedPassw>

val LazyPassw.id get() = placeholder

fun dummyLazyPassw() = LazyModelProvider(1L) {
    delay(Random.nextLong(3000))
    DecryptedPassw(passw = "passw")
}
