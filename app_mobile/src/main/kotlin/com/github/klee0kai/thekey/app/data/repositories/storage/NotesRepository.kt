package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.coloredNote
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.domain.model.DebugConfigs
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.coroutine.collectTo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicInteger

class NotesRepository(
    val identifier: StorageIdentifier,
) {

    private val engine = DI.cryptStorageEngineSafeLazy(identifier)
    private val scope = DI.defaultThreadScope()

    private val consumers = AtomicInteger(0)
    private val _notes = MutableStateFlow<List<ColoredNote>>(emptyList())
    val notes = channelFlow {
        consumers.incrementAndGet()
        loadNotes()
        _notes.collectTo(this)
        awaitClose { consumers.decrementAndGet() }
    }

    suspend fun note(notePtr: Long) = engine().note(notePtr)

    suspend fun setNotesGroup(notesPtr: List<Long>, groupId: Long) {
        if (DebugConfigs.isNotesFastUpdate) {
            _notes.update { list ->
                list.map { note ->
                    if (note.ptnote in notesPtr) {
                        note.copy(group = ColorGroup(id = groupId))
                    } else {
                        note
                    }
                }
            }
        }

        engine().setNotesGroup(notesPtr.toTypedArray(), groupId)
        loadNotes(force = true)
    }

    suspend fun saveNote(note: DecryptedNote, setAll: Boolean = false) {
        if (DebugConfigs.isNotesFastUpdate) {
            _notes.update { list ->
                list.filter { it.ptnote != note.ptnote } +
                        listOf(note.coloredNote(isLoaded = true))
            }
        }

        engine().saveNote(note, setAll = setAll)
        loadNotes(force = true)
    }

    suspend fun removeNote(noteptr: Long) {
        if (DebugConfigs.isNotesFastUpdate) {
            _notes.update { list -> list.filter { it.ptnote != noteptr } }
        }

        engine().removeNote(noteptr)
        loadNotes(force = true)
    }


    suspend fun removeHist(histPtr: Long) {
        engine().removeHist(histPtr)
        loadNotes(force = true)
    }

    suspend fun clearCache() {
        _notes.update { emptyList() }
    }

    private fun loadNotes(force: Boolean = false) = scope.launch {
        if (_notes.value.isNotEmpty() && !force) return@launch
        if (consumers.get() <= 0) {
            // no consumers
            _notes.value = emptyList()
            return@launch
        }

        if (_notes.value.isEmpty()) {
            _notes.value = engine().notes()
                .map { it.coloredNote(isLoaded = false) }
        }

        _notes.value = engine().notes(info = true)
            .map { it.coloredNote(isLoaded = true) }
    }


}