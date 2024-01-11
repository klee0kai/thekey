package com.github.klee0kai.thekey.app.ui.note

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.navigation.back
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NotePresenter(
    val storagePath: String,
    val notePtr: Long = 0,
) {
    private val scope = DI.mainThreadScope()
    private val navigator = DI.navigator()
    private val engine = DI.cryptStorageEngineLazy(StorageIdentifier(path = storagePath))

    fun note(): Deferred<DecryptedNote> = scope.async {
        engine().note(notePtr)
    }

    fun save(note: DecryptedNote) = scope.launch {
        val error = engine().saveNote(note)

        navigator.back()
    }


}