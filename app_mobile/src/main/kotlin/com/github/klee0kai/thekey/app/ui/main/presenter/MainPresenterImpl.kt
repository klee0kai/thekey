package com.github.klee0kai.thekey.app.ui.main.presenter

import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.flow.flow

class MainPresenterImpl : MainPresenter {

    private val scope = DI.defaultThreadScope()
    private val settings = DI.settingsRepositoryLazy()

    override val loginSecureMode = flow {
        settings().loginSecure.flow.collect(this)
    }

}