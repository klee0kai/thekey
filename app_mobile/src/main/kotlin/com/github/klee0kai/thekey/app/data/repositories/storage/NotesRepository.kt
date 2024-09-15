package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.coloredNote
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.domain.model.DebugConfigs
import com.github.klee0kai.thekey.core.utils.coroutine.lazyStateFlow
import kotlinx.coroutines.flow.update

class NotesRepository(
    val identifier: StorageIdentifier,
) {

    private val engine = DI.cryptStorageEngineSafeLazy(identifier)
    private val scope = DI.defaultThreadScope()

    val notes = lazyStateFlow(
        init = emptyList<ColoredNote>(),
        defaultArg = false,
        scope = scope
    ) { force ->
        if (value.isNotEmpty() && !force) return@lazyStateFlow

        if (value.isEmpty()) {
            value = engine().notes()
                .map { it.coloredNote(isLoaded = false) }
        }

        value = engine().notes(info = true)
            .map { it.coloredNote(isLoaded = true) }
    }


    suspend fun note(notePtr: Long) = engine().note(notePtr)

    suspend fun setNotesGroup(notesPtr: List<Long>, groupId: Long) {
        if (DebugConfigs.isNotesFastUpdate) {
            notes.update { list ->
                list.map { note ->
                    if (note.id in notesPtr) {
                        note.copy(group = ColorGroup(id = groupId))
                    } else {
                        note
                    }
                }
            }
        }

        engine().setNotesGroup(notesPtr.toTypedArray(), groupId)
        notes.touch(true)
    }

    suspend fun saveNote(note: DecryptedNote, setAll: Boolean = false) {
        if (DebugConfigs.isNotesFastUpdate) {
            notes.update { list ->
                list.filter { it.id != note.ptnote } +
                        listOf(note.coloredNote(isLoaded = true))
            }
        }

        engine().saveNote(note, setAll = setAll)
        notes.touch(true)
    }

    suspend fun removeNote(noteptr: Long) {
        if (DebugConfigs.isNotesFastUpdate) {
            notes.update { list -> list.filter { it.id != noteptr } }
        }

        engine().removeNote(noteptr)
        notes.touch(true)
    }

    suspend fun moveNote(
        notePt: Long,
        targetIdentifier: StorageIdentifier,
    ) {
        if (DebugConfigs.isNotesFastUpdate) {
            notes.update { list -> list.filter { it.id != notePt } }
        }
        engine().moveNote(
            notePt = notePt,
            targetStoragePath = targetIdentifier.path,
            targetEngineIdentifier = targetIdentifier.engineIdentifier,
        )
        DI.notesRepLazy(targetIdentifier).get().notes.touch(true)
        notes.touch(true)
    }

    suspend fun removeHist(histPtr: Long) {
        engine().removeHist(histPtr)
        notes.touch(true)
    }

    suspend fun clearCache() {
        notes.value = emptyList()
    }

}