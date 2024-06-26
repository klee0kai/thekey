package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.coloredNote
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class NotesRepository(
    val identifier: StorageIdentifier,
) {

    val engine = DI.cryptStorageEngineSafeLazy(identifier)

    val notes = MutableStateFlow<List<ColoredNote>>(emptyList())

    suspend fun loadNotes() {
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
        loadNotes()
    }

    suspend fun saveNote(note: DecryptedNote, setAll: Boolean = false) {
        engine().saveNote(note, setAll = setAll)
        loadNotes()
    }

    suspend fun removeNote(noteptr: Long) {
        engine().removeNote(noteptr)
        loadNotes()
    }

    suspend fun generateNewPassw(params: GenPasswParams) =
        engine().generateNewPassw(params)

    suspend fun clear() {
        notes.update { emptyList() }
    }

}