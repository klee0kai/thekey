package com.github.klee0kai.thekey.app.ui.note

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.isEmpty
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.async

class NotePresenter(
    val identifier: NoteIdentifier,
) {
    private val scope = DI.defaultThreadScope()
    private val navigator = DI.router()
    private val engine = DI.cryptStorageEngineLazy(identifier.storage())

    fun note() = scope.async {
        engine()?.note(identifier.notePtr)
    }

    fun save(note: DecryptedNote) = scope.launchLatest("safe") {
        if (note.isEmpty()) {
            navigator.snack(R.string.note_is_empty)
            return@launchLatest
        }

        val error = engine()?.saveNote(note)
        navigator.back()
    }


}