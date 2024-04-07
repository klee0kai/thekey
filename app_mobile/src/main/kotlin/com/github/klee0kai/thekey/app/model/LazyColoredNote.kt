package com.github.klee0kai.thekey.app.model

import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModel
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias LazyColoredNote = LazyModel<Long, ColoredNote>

val LazyColoredNote.id get() = placeholder

fun dummyLazyColoredNote() = LazyModelProvider(1L) {
    delay(Random.nextLong(3000))
    ColoredNote(site = "site")
}
