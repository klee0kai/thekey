package com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter

import com.github.klee0kai.thekey.app.ui.storage.genpassw.model.GenPasswState
import kotlinx.coroutines.flow.MutableStateFlow

open class GenPasswPresenterDummy(
    state: GenPasswState = GenPasswState(
        passwLen = 6,
        symInPassw = true,
        passw = "Si@3AP"
    )
) : GenPasswPresenter {

    override val state = MutableStateFlow(state)

}