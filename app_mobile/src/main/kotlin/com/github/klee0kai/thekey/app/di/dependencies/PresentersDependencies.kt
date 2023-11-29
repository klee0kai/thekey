package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.ui.login.LoginPresenter

interface PresentersDependencies {

    fun mainViewModule(): LoginPresenter

}