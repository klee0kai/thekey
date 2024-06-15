package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.coloredNote
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class OtpNotesRepository(
    val identifier: StorageIdentifier,
) {

    val engine = DI.cryptStorageEngineSafeLazy(identifier)
    val otpNotes = MutableStateFlow<List<ColoredOtpNote>>(emptyList())

    suspend fun loadOtpNotes() {
        if (otpNotes.value.isEmpty()) {
            otpNotes.value = engine().otpNotes()
                .map { it.coloredNote(isLoaded = false) }
        }

        otpNotes.value = engine().otpNotes(info = true)
            .map { it.coloredNote(isLoaded = true) }
    }

    suspend fun otpNote(notePtr: Long) = engine().otpNote(notePtr)

    suspend fun setOtpNotesGroup(notesPtr: List<Long>, groupId: Long) {
        engine().setNotesGroup(notesPtr.toTypedArray(), groupId)
        loadOtpNotes()
    }

    suspend fun saveOtpNote(note: DecryptedOtpNote, setAll: Boolean = false) {
        engine().saveOtpNote(note, setAll = setAll)
        loadOtpNotes()
    }

    suspend fun removeOtpNote(noteptr: Long) {
        engine().removeOtpNote(noteptr)
        loadOtpNotes()
    }

    suspend fun otpNoteFromUrl(url: String) = engine().otpNoteFromUrl(url)

    suspend fun clear() {
        otpNotes.update { emptyList() }
    }

}