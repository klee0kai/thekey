package com.github.klee0kai.thekey.app.ui.otpnote.presenter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.editNoteDest
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val otpNoteShadow = MutableStateFlow<ColoredOtpNote?>(null)
    override val otpNote = flow {
        interactor().otpNoteUpdates(identifier.otpNotePtr)
            .collect {
                otpNoteShadow.emit(it)
                emit(it)
            }
    }

    override fun edit(router: AppRouter?) = scope.launch {
        router?.back()?.join()
        router?.navigate(identifier.editNoteDest())
    }

    override fun copyIssuer(
        router: AppRouter?,
    ) = scope.launch {
        val otp = otpNoteShadow.value ?: return@launch
        val data = ClipData.newPlainText("Issuer", otp.issuer)
        clipboardManager.setPrimaryClip(data)
        router?.snack(R.string.copied_to_clipboard)
    }

    override fun copyName(
        router: AppRouter?,
    ) = scope.launch {
        val otp = otpNoteShadow.value ?: return@launch
        val data = ClipData.newPlainText("Name", otp.name)
        clipboardManager.setPrimaryClip(data)
        router?.snack(R.string.copied_to_clipboard)
    }

    override fun copyCode(
        router: AppRouter?,
    ) = scope.launch {
        val otp = otpNoteShadow.value ?: return@launch
        val data = ClipData.newPlainText("OTP", otp.otpPassw)
        clipboardManager.setPrimaryClip(data)
        router?.snack(R.string.copied_to_clipboard)
    }

    override fun increment(
        router: AppRouter?,
    ) = scope.launch {

    }

}