package com.github.klee0kai.thekey.app.data.model

import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModel
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias LazyNote = LazyModel<Long, DecryptedNote>

val LazyNote.id get() = placeholder

fun dummyLazyNote() = LazyModelProvider(1L) {
    delay(Random.nextLong(3000))
    DecryptedNote(site = "site")
}
