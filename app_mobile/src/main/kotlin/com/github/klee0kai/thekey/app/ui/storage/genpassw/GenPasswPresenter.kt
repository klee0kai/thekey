package com.github.klee0kai.thekey.app.ui.storage.genpassw

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GenPasswPresenter(
    val storageIdentifier: StorageIdentifier,
) {

    private val clipboardManager by lazy { DI.app().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager }
    private val engine = DI.cryptStorageEngineLazy(storageIdentifier)
    private val router = DI.router()
    private val scope = DI.mainThreadScope()

    /**
     * @see `tkcore/storage1/salt/salt1.h`
     */
    val passwLenRange = (4..16)

    val passwLen = MutableStateFlow(passwLenRange.first)
    val symInPassw = MutableStateFlow(false)
    val specSymbolsInPassw = MutableStateFlow(false)
    val passw = MutableStateFlow("")

    fun init() {
        scope.launch {
            passw.value = engine().lastGeneratedPassw()
        }
    }

    fun generatePassw() = scope.launch {
        val newPassw = engine().generateNewPassw(
            GenPasswParams(
                len = passwLen.value,
                symbolsInPassw = symInPassw.value,
                specSymbolsInPassw = specSymbolsInPassw.value
            )
        )

        passw.value = newPassw
    }

    fun copyToClipboard() = scope.launch {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Password", passw.value))

        router.snack(R.string.copied_to_clipboard)
    }


}