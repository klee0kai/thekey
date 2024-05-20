package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColoredNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.utils.common.launchLatest
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

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
    }

    fun loadNotes() = scope.launchLatest("load_notes") { rep().loadNotes() }

    fun note(notePtr: Long) = scope.async { rep().note(notePtr) }

    fun saveNote(note: DecryptedNote, setAll: Boolean = false) = scope.launch { rep().saveNote(note, setAll) }

    fun removeNote(noteptr: Long) = scope.launch { rep().removeNote(noteptr) }

    fun setNotesGroup(notesPtr: List<Long>, groupId: Long) = scope.launch { rep().setNotesGroup(notesPtr, groupId) }

    fun generateNewPassw(params: GenPasswParams) = scope.async { rep().generateNewPassw(params) }

    fun clear() = scope.launchLatest("clear") { rep().clear() }

}