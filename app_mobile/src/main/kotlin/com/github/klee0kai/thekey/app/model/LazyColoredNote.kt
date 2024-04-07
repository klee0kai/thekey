package com.github.klee0kai.thekey.app.model

import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModel
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider

typealias LazyColoredNote = LazyModel<Long, ColoredNote>

val LazyColoredNote.id get() = placeholder

fun dummyLazyColoredNote() = LazyModelProvider(1L) { ColoredNote() }
