package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.model.LazyNote
import com.github.klee0kai.thekey.app.utils.common.fromPreloadedOrCreate
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesRepository(
    val identifier: StorageIdentifier,
) {

    val scope = DI.defaultThreadScope()
    val engine = DI.cryptStorageEngineSafeLazy(identifier)

    val notes = MutableStateFlow<List<LazyNote>>(emptyList())

    fun loadNotes(forceDirty: Boolean = false) = scope.launchLatest("load_notes") {
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

    fun note(notePtr: Long): Deferred<DecryptedNote> = scope.async { engine().note(notePtr) }

    fun saveNote(note: DecryptedNote, setAll: Boolean = false) = scope.launch {
        engine().saveNote(note, setAll = setAll)
        loadNotes(forceDirty = true)
    }

    fun removeNote(noteptr: Long) = scope.launch {
        engine().removeNote(noteptr)
        loadNotes(forceDirty = true)
    }

    fun generateNewPassw(params: GenPasswParams): Deferred<String> = scope.async {
        engine().generateNewPassw(params)
    }

    fun clear() = scope.launchLatest("clear") {
        notes.update { emptyList() }
    }


}