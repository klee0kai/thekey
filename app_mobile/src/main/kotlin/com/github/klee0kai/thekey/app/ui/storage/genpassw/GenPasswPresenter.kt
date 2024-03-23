package com.github.klee0kai.thekey.app.ui.storage.genpassw

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GenPasswPresenter(
    val storageIdentifier: StorageIdentifier,
) {

    private val engine = DI.cryptStorageEngineLazy(storageIdentifier)
    private val router = DI.router()
    private val scope = DI.mainThreadScope()

    val passwLenRange = (4..12)
    val passwLen = MutableStateFlow(passwLenRange.first)
    val symInPassw = MutableStateFlow(false)
    val specSymbolsInPassw = MutableStateFlow(false)
    val passw = MutableStateFlow("")

    init {
        scope.launch {

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