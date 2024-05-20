package com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.navigation.createNoteDest
import com.github.klee0kai.thekey.app.ui.storage.genpassw.model.GenPasswState
import com.github.klee0kai.thekey.app.ui.storage.genpassw.model.toGenParams
import com.github.klee0kai.thekey.core.utils.common.launchLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class GenPasswPresenterImpl(
    val storageIdentifier: StorageIdentifier,
) : GenPasswPresenter {

    private val clipboardManager by lazy { DI.ctx().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager }
    private val settings = DI.settingsRepositoryLazy()
    private val engine = DI.cryptStorageEngineSafeLazy(storageIdentifier)
    private val router = DI.router()
    private val scope = DI.defaultThreadScope()

    override val state = MutableStateFlow(GenPasswState())

    override fun init() = scope.launchLatest("init") {
        state.value = GenPasswState(
            passwLen = settings().genPasswLen(),
            symInPassw = settings().genPasswIncludeSymbols(),
            specSymbolsInPassw = settings().genPasswIncludeSpecSymbols(),
            passw = engine().lastGeneratedPassw(),
        )
    }


    override fun generatePassw() = scope.launchLatest("gen_passw") {
        val newPassw = engine().generateNewPassw(state.value.toGenParams())

        state.update {
            it.copy(passw = newPassw)
        }
    }

    override fun copyToClipboard() = scope.launchLatest("copy_clipboard", DI.mainDispatcher()) {
        val data = ClipData.newPlainText("Password", state.value.passw)
        clipboardManager.setPrimaryClip(data)

        router.snack(R.string.copied_to_clipboard)
    }

    override fun saveAsNewNote() = scope.launchLatest("save") {
        router.navigate(storageIdentifier.createNoteDest(DecryptedNote(passw = state.value.passw)))
    }

    override fun input(block: GenPasswState.() -> GenPasswState) = scope.launchLatest("input") {
        val newState = block.invoke(state.value)

        launch { settings().genPasswLen.set(newState.passwLen) }
        launch { settings().genPasswIncludeSymbols.set(newState.symInPassw) }
        launch { settings().genPasswIncludeSpecSymbols.set(newState.specSymbolsInPassw) }
        state.value = newState
    }
}