package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.data.model.LazyNote
import com.github.klee0kai.thekey.app.data.model.id
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.utils.lazymodel.fromPreloadedOrCreate
import com.github.klee0kai.thekey.app.utils.lazymodel.fullValue
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
                        if (forceDirty) dirty()
                    }
                }
        }
    }

    suspend fun note(notePtr: Long) = engine().note(notePtr)

    suspend fun setNoteGroup(notePt: Long, groupId: Long) {
        val newNote = notes.value.firstOrNull { it.id == notePt }
            ?.fullValue()
            ?.copy(colorGroupId = groupId)
            ?: return
        engine().saveNote(newNote)
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