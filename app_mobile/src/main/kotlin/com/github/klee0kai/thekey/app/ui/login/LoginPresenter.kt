package com.github.klee0kai.thekey.app.ui.login

import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.launch

class LoginPresenter {

    val scope = DI.mainThreadScope()

    val navigator = DI.navigator()

    fun login() {
        scope.launch {

        }
    }

}