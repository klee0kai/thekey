package com.github.klee0kai.thekey.app.domain.model

import com.github.klee0kai.thekey.app.utils.common.DummyId
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModel
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider

typealias LazyColoredNote = LazyModel<Long, ColoredNote>

val LazyColoredNote.id get() = placeholder

fun dummyLazyColoredNoteSkeleton() = LazyModelProvider(DummyId.dummyId) { ColoredNote() }

fun dummyLazyColoredNoteLoaded(note: ColoredNote = ColoredNote()) = LazyModelProvider(placeholder = DummyId.dummyId, preloaded = note) { ColoredNote() }
