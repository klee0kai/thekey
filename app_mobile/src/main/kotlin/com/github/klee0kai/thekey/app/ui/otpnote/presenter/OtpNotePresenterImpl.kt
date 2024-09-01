package com.github.klee0kai.thekey.app.ui.otpnote.presenter

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.editNoteDest
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull

class OtpNotePresenterImpl(
    val identifier: NoteIdentifier,
) : OtpNotePresenter {

    private val dateFormat by lazy { TimeFormats.simpleDateFormat() }
    private val scope = DI.defaultThreadScope()
    private val notesInteractor = DI.otpNotesInteractorLazy(identifier.storage())
    private val clipboardManager by lazy {
        DI.ctx().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val _note = MutableStateFlow<ColoredOtpNote?>(null)
    override val note = _note.filterNotNull()

    override fun init() = scope.launch {
        _note.value = notesInteractor().otpNotes.firstOrNull()
            ?.firstOrNull { identifier.notePtr == it.ptnote }
            ?.copy(isLoaded = false)
        val passw = notesInteractor().otpNote(identifier.notePtr).await()
//        _note.update { it?.copy(passw = passw, isLoaded = true) }
    }

    override fun edit(router: AppRouter?) = scope.launch {
        router?.back()?.join()
        router?.navigate(identifier.editNoteDest())
    }


}