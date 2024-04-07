package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.model.LazyNote
import com.github.klee0kai.thekey.app.utils.common.fromPreloadedOrCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class NotesRepository(
    val identifier: StorageIdentifier,
) {

    val engine = DI.cryptStorageEngineSafeLazy(identifier)
    val notes = MutableStateFlow<List<LazyNote>>(emptyList())

    suspend fun loadNotes(forceDirty: Boolean = false) {
        notes.update { oldNotes ->
            engine()
                .notes()
                .map { noteLite ->
                    fromPreloadedOrCreate(noteLite.ptnote, oldNotes) {
                        withContext(DI.defaultDispatcher()) {
                            engine().note(noteLite.ptnote)
                        }
                    }.apply {
                        dirty = forceDirty
                    }
                }
        }
    }

    suspend fun note(notePtr: Long) = engine().note(notePtr)

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