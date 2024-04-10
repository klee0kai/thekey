package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.domain.model.LazyColoredNote
import com.github.klee0kai.thekey.app.domain.model.coloredNote
import com.github.klee0kai.thekey.app.domain.model.id
import com.github.klee0kai.thekey.app.domain.model.noGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import com.github.klee0kai.thekey.app.utils.lazymodel.fullValue
import com.github.klee0kai.thekey.app.utils.lazymodel.map
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

    val notes = flow<List<LazyColoredNote>> {
        combine(
            flow = rep().notes,
            flow2 = groupsRep().groups,
        ) { notes, groups ->
            notes.map {
                it.map { note ->
                    val group = groups.firstOrNull { it.id == note.colorGroupId }?.fullValue()
                    note.coloredNote(group = group ?: ColorGroup.noGroup())
                }
            }
        }.collect(this)
    }

    fun loadNotes(forceDirty: Boolean = false) = scope.launchLatest("load_notes") { rep().loadNotes(forceDirty) }

    fun note(notePtr: Long) = scope.async { rep().note(notePtr) }

    fun saveNote(note: DecryptedNote, setAll: Boolean = false) = scope.launch { rep().saveNote(note, setAll) }

    fun removeNote(noteptr: Long) = scope.launch { rep().removeNote(noteptr) }

    fun setNotesGroup(notesPtr: List<Long>, groupId: Long) = scope.launch { rep().setNotesGroup(notesPtr, groupId) }

    fun generateNewPassw(params: GenPasswParams) = scope.async { rep().generateNewPassw(params) }

    fun clear() = scope.launchLatest("clear") { rep().clear() }

}