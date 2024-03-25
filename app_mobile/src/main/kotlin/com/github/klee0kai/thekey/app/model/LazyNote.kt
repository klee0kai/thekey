package com.github.klee0kai.thekey.app.model

import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.utils.common.LazyModel
import kotlinx.coroutines.delay
import kotlin.random.Random

typealias LazyNote = LazyModel<DecryptedNote, DecryptedNote>

fun dummyLazyNote() = LazyNote(DecryptedNote()) {
    delay(Random.nextLong(3000))
    DecryptedNote(site = "site")
}
