package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class OtpNotesInteractor(
    val identifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.otpNotesRepLazy(identifier)
    private val groupsRep = DI.groupRepLazy(identifier)

    val otpNotes = flow<List<ColoredOtpNote>> {
        combine(
            flow = rep().otpNotes,
            flow2 = groupsRep().groups,
        ) { notes, groups ->
            notes.map { note ->
                val group = groups.firstOrNull { it.id == note.group.id } ?: note.group
                note.copy(group = group)
            }
        }.collect(this)
    }

    fun loadOtpNotes() = scope.launchLatest("load_notes") { rep().loadOtpNotes() }

    fun otpNote(notePtr: Long) = scope.async { rep().otpNote(notePtr) }

    fun saveOtpNote(note: DecryptedOtpNote, setAll: Boolean = false) = scope.launch { rep().saveOtpNote(note, setAll) }

    fun removeOtpNote(noteptr: Long) = scope.launch { rep().removeOtpNote(noteptr) }

    fun setOtpNotesGroup(notesPtr: List<Long>, groupId: Long) = scope.launch { rep().setOtpNotesGroup(notesPtr, groupId) }

    suspend fun otpNoteFromUrl(url: String) = rep().otpNoteFromUrl(url)

    fun clear() = scope.launchLatest("clear") { rep().clear() }

}