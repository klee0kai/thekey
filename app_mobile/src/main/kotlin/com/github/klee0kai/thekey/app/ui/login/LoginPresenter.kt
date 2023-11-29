package com.github.klee0kai.thekey.app.ui.login

import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.launch

class LoginPresenter {

    private val storagesRep by DI.storagesRepositoryLazy()
    private val settingsRep by DI.settingsRepositoryLazy()
    private val navigator = DI.navigator()
    private val scope = DI.mainThreadScope()

    fun login(passw: String) {
        scope.launch {

        }
    }

}