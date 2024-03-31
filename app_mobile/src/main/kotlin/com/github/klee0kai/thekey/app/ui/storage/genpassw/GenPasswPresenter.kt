package com.github.klee0kai.thekey.app.ui.storage.genpassw

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GenPasswPresenter(
    val storageIdentifier: StorageIdentifier,
) {

    private val clipboardManager by lazy { DI.app().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager }
    private val settings = DI.settingsRepositoryLazy()
    private val engine = DI.cryptStorageEngineSafeLazy(storageIdentifier)
    private val router = DI.router()
    private val scope = DI.defaultThreadScope()

    /**
     * @see `tkcore/storage1/salt/salt1.h`
     */
    val passwLenRange = (4..16)

    val passwLen = MutableStateFlow(passwLenRange.first)
    val symInPassw = MutableStateFlow(false)
    val specSymbolsInPassw = MutableStateFlow(false)
    val passw = MutableStateFlow("")

    fun init() = scope.launchLatest("init") {
        passwLen.value = settings().genPasswLen()
        symInPassw.value = settings().genPasswIncludeSymbols()
        specSymbolsInPassw.value = settings().genPasswIncludeSpecSymbols()
        passw.value = engine()?.lastGeneratedPassw() ?: ""

        // subscribe on user changes
        launch { passwLen.collect { settings().genPasswLen.set(it) } }
        launch { symInPassw.collect { settings().genPasswIncludeSymbols.set(it) } }
        launch { specSymbolsInPassw.collect { settings().genPasswIncludeSpecSymbols.set(it) } }
    }


    fun generatePassw() = scope.launchLatest("gen_passw") {
        val newPassw = engine()?.generateNewPassw(
            GenPasswParams(
                len = passwLen.value,
                symbolsInPassw = symInPassw.value,
                specSymbolsInPassw = specSymbolsInPassw.value
            )
        ) ?: return@launchLatest

        passw.value = newPassw
    }

    fun copyToClipboard() = scope.launchLatest("copy_clipboard", DI.mainDispatcher()) {
        val data = ClipData.newPlainText("Password", passw.value)
        clipboardManager.setPrimaryClip(data)

        router.snack(R.string.copied_to_clipboard)
    }


}