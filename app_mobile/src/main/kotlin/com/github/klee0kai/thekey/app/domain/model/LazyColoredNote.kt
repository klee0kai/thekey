package com.github.klee0kai.thekey.app.domain.model

import com.github.klee0kai.thekey.app.utils.common.Dummy
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModel
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider

typealias LazyColoredNote = LazyModel<Long, ColoredNote>

val LazyColoredNote.id get() = placeholder

fun dummyLazyColoredNoteSkeleton() = LazyModelProvider(Dummy.dummyId) { ColoredNote() }

fun dummyLazyColoredNoteLoaded(note: ColoredNote = ColoredNote()) = LazyModelProvider(placeholder = Dummy.dummyId, preloaded = note) { ColoredNote() }
