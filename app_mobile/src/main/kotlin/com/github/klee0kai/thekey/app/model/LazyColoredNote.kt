package com.github.klee0kai.thekey.app.model

import com.github.klee0kai.thekey.app.utils.common.LazyModel
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias LazyColoredNote = LazyModel<Long, ColoredNote>

val LazyColoredNote.id get() = placeholder

fun dummyLazyColoredNote() = LazyColoredNote(1) {
    delay(Random.nextLong(3000))
    ColoredNote(site = "site")
}
