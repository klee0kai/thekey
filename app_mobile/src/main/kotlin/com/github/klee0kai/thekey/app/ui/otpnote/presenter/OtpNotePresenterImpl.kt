package com.github.klee0kai.thekey.app.ui.otpnote.presenter

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.editNoteDest
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.flow

class OtpNotePresenterImpl(
    val identifier: NoteIdentifier,
) : OtpNotePresenter {

    private val dateFormat by lazy { TimeFormats.simpleDateFormat() }
    private val scope = DI.defaultThreadScope()
    private val interactor = DI.otpNotesInteractorLazy(identifier.storage())
    private val clipboardManager by lazy {
        DI.ctx().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    override val note = flow {
        interactor().otpNoteUpdates(identifier.otpNotePtr).collect(this)
    }

    override fun edit(router: AppRouter?) = scope.launch {
        router?.back()?.join()
        router?.navigate(identifier.editNoteDest())
    }


}