package com.github.klee0kai.thekey.app.ui.note.presenter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.editNoteDest
import com.github.klee0kai.thekey.app.ui.navigation.histDest
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.domain.model.updateWith
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update

class NotePresenterImpl(
    val identifier: NoteIdentifier,
) : NotePresenter {

    private val dateFormat by lazy { TimeFormats.simpleDateFormat() }
    private val scope = DI.defaultThreadScope()
    private val notesInteractor = DI.notesInteractorLazy(identifier.storage())
    private val clipboardManager by lazy {
        DI.ctx().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val _note = MutableStateFlow<ColoredNote?>(null)
    override val note = _note.filterNotNull()

    override fun init() = scope.launch {
        _note.value = notesInteractor().notes.firstOrNull()
            ?.firstOrNull { identifier.notePtr == it.id }
            ?.updateWith(dateFormat)
            ?.copy(isLoaded = false)
        val passw = notesInteractor().note(identifier.notePtr).await().passw
        _note.update { it?.copy(passw = passw, isLoaded = true) }
    }

    override fun showHistory(router: AppRouter?) = scope.launch {
        router?.back()?.join()
        router?.navigate(identifier.histDest())
    }

    override fun edit(router: AppRouter?) = scope.launch {
        router?.back()?.join()
        router?.navigate(identifier.editNoteDest())
    }

    override fun copySite(router: AppRouter?) = scope.launch {
        val data = ClipData.newPlainText("Site", _note.value?.site)
        clipboardManager.setPrimaryClip(data)
        router?.snack(R.string.copied_to_clipboard)
    }

    override fun copyLogin(router: AppRouter?) = scope.launch {
        val data = ClipData.newPlainText("Login", _note.value?.login)
        clipboardManager.setPrimaryClip(data)
        router?.snack(R.string.copied_to_clipboard)
    }

    override fun copyPassw(router: AppRouter?) = scope.launch {
        val data = ClipData.newPlainText("Password", _note.value?.passw)
        clipboardManager.setPrimaryClip(data)
        router?.snack(R.string.copied_to_clipboard)
    }

    override fun copyDesc(router: AppRouter?) = scope.launch {
        val data = ClipData.newPlainText("Description", _note.value?.desc)
        clipboardManager.setPrimaryClip(data)
        router?.snack(R.string.copied_to_clipboard)
    }


}