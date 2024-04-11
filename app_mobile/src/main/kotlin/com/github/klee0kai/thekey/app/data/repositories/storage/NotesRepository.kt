package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.domain.model.ColoredNote
import com.github.klee0kai.thekey.app.domain.model.coloredNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class NotesRepository(
    val identifier: StorageIdentifier,
) {

    val engine = DI.cryptStorageEngineSafeLazy(identifier)

    val notes = MutableStateFlow<List<ColoredNote>>(emptyList())

    suspend fun loadNotes(forceDirty: Boolean = false) {
        if (notes.value.isEmpty()) {
            notes.value = engine().notes()
                .map { it.coloredNote(isLoaded = false) }
        }

        notes.value = engine().notes(info = true)
            .map { it.coloredNote(isLoaded = true) }
    }

    suspend fun note(notePtr: Long) = engine().note(notePtr)

    suspend fun setNotesGroup(notesPtr: List<Long>, groupId: Long) {
        engine().setNotesGroup(notesPtr.toTypedArray(), groupId)
        loadNotes(forceDirty = true)
    }

    suspend fun saveNote(note: DecryptedNote, setAll: Boolean = false) {
        engine().saveNote(note, setAll = setAll)
        loadNotes(forceDirty = true)
    }

    suspend fun removeNote(noteptr: Long) {
        engine().removeNote(noteptr)
        loadNotes(forceDirty = true)
    }

    suspend fun generateNewPassw(params: GenPasswParams) =
        engine().generateNewPassw(params)


    suspend fun clear() {
        notes.update { emptyList() }
    }


}