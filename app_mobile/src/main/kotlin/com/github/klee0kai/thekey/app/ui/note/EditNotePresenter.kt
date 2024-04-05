package com.github.klee0kai.thekey.app.ui.note

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.engine.model.isEmpty
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import com.github.klee0kai.thekey.app.utils.common.launchLatestSafe
import com.github.klee0kai.thekey.app.utils.common.launchSafe
import kotlinx.coroutines.flow.MutableStateFlow

class EditNotePresenter(
    val identifier: NoteIdentifier,
) {
    private val scope = DI.defaultThreadScope()
    private val navigator = DI.router()
    private val engine = DI.cryptStorageEngineSafeLazy(identifier.storage())

    val originNote = MutableStateFlow<DecryptedNote?>(null)
    val note = MutableStateFlow(DecryptedNote())

    fun init(prefilled: DecryptedNote? = null) = scope.launchSafe {
        if (identifier.notePtr == 0L) {
            originNote.value = prefilled ?: DecryptedNote()
        } else {
            originNote.value = engine()?.note(identifier.notePtr) ?: DecryptedNote()
        }
        note.value = originNote.value!!
    }


    fun save() = scope.launchLatest("safe") {
        val note = note.value
        if (note.isEmpty()) {
            navigator.snack(R.string.note_is_empty)
            return@launchLatest
        }

        val error = engine()?.saveNote(note, setAll = true)
        navigator.back()
    }

    fun generate() = scope.launchLatestSafe("gen") {
        val newPassw = engine()?.generateNewPassw(GenPasswParams(oldPassw = note.value.passw))
            ?: return@launchLatestSafe

        note.value = note.value.copy(passw = newPassw)
    }

}