package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.coloredNote
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.common.launchLatest
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NotesInteractor(
    val identifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.notesRepLazy(identifier)
    private val groupsRep = DI.groupRepLazy(identifier)

    val notes = flow<List<ColoredNote>> {
        combine(
            flow = rep().notes,
            flow2 = groupsRep().groups,
        ) { notes, groups ->
            notes.map { note ->
                val group = groups.firstOrNull { it.id == note.group.id } ?: note.group
                note.copy(group = group)
            }
        }.collect(this)
    }.flowOn(DI.defaultDispatcher())

    val loadedNotes = notes
        .filter { list -> list.all { it.isLoaded } }
        .flowOn(DI.defaultDispatcher())

    fun note(
        notePtr: Long,
    ): Deferred<ColoredNote> = scope.async {
        rep().note(notePtr)
            .coloredNote(
                isLoaded = true,
                isHistLoaded = true,
            )
    }

    @Deprecated("use domain models")
    fun decryptedNote(
        notePtr: Long,
    ): Deferred<DecryptedNote> = scope.async {
        rep().note(notePtr)
    }

    fun saveNote(
        note: DecryptedNote,
        setAll: Boolean = false,
    ) = scope.launch {
        rep().saveNote(note, setAll)
    }

    fun setNotesGroup(
        notesPtr: List<Long>,
        groupId: Long,
    ) = scope.launch {
        rep().setNotesGroup(notesPtr, groupId)
    }

    fun removeNote(
        noteptr: Long,
    ) = scope.launch {
        rep().removeNote(noteptr)
    }

    fun moveNote(
        notePt: Long,
        targetIdentifier: StorageIdentifier,
    ) = scope.launch {
        rep().moveNote(notePt,targetIdentifier)
    }


    fun removeHist(
        histPtr: Long,
    ) = scope.launch {
        rep().removeHist(histPtr)
    }


    fun clearCache() = scope.launchLatest("clear") {
        rep().clearCache()
    }


}