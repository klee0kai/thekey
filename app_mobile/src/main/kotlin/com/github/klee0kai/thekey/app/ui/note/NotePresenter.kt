package com.github.klee0kai.thekey.app.ui.note

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.navigation.storageIdentifier
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NotePresenter(
    val identifier: NoteIdentifier,
) {
    private val scope = DI.mainThreadScope()
    private val navigator = DI.router()
    private val engine = DI.cryptStorageEngineLazy(identifier.storageIdentifier())

    fun note(): Deferred<DecryptedNote> = scope.async {
        engine().note(identifier.notePtr)
    }

    fun save(note: DecryptedNote) = scope.launch {
        val error = engine().saveNote(note)

        navigator.back()
    }


}