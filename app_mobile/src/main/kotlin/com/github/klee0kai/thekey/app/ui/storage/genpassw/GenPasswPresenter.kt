package com.github.klee0kai.thekey.app.ui.storage.genpassw

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class GenPasswPresenter(
    val storageIdentifier: StorageIdentifier,
) {

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


}