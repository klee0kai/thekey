package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.utils.common.launchLatest
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class OtpNotesInteractor(
    val identifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.otpNotesRepLazy(identifier)
    private val groupsRep = DI.groupRepLazy(identifier)

    private val groups = flow { groupsRep().groups.collect(this) }

    val otpNotes = flow<List<ColoredOtpNote>> {
        combine(
            flow = rep().otpNotes,
            flow2 = groups,
        ) { notes, groups ->
            notes.map { note ->
                val group = groups.firstOrNull { it.id == note.group.id } ?: note.group
                note.copy(group = group)
            }
        }.collect(this)
    }

    val loadedOtpNotes = otpNotes.filter { list -> list.all { it.isLoaded } }

    fun otpNoteUpdates(
        notePtr: Long,
        increment: Boolean,
    ) = flow {
        combine(
            flow = rep().otpNotePinUpdates(notePtr, increment),
            flow2 = groups,
        ) { note, groups ->
            val group = groups.firstOrNull { it.id == note.group.id } ?: note.group
            note.copy(group = group)
        }.collect(this)
    }

    @Deprecated("use domain models")
    fun otpDecryptedNote(
        notePtr: Long,
    ) = scope.async {
        rep().otpNote(notePtr)
    }


    fun saveOtpNote(
        note: DecryptedOtpNote,
        setAll: Boolean = false,
    ) = scope.launch {
        rep().saveOtpNote(note, setAll)
    }

    fun removeOtpNote(
        noteptr: Long,
    ) = scope.launch {
        rep().removeOtpNote(noteptr)
    }

    fun setOtpNotesGroup(
        notesPtr: List<Long>,
        groupId: Long,
    ) = scope.launch {
        rep().setOtpNotesGroup(notesPtr, groupId)
    }

    suspend fun otpNoteFromUrl(
        url: String,
    ) = rep().otpNoteFromUrl(url)

    fun clearCache() = scope.launchLatest("clear") { rep().clearCache() }

}