package com.github.klee0kai.thekey.app.model

import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import com.github.klee0kai.thekey.app.utils.common.LazyModel
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias LazyPassw = LazyModel<DecryptedPassw, DecryptedPassw>

fun dummyLazyPassw() = LazyPassw(DecryptedPassw()) {
    delay(Random.nextLong(3000))
    DecryptedPassw(passw = "passw")
}
