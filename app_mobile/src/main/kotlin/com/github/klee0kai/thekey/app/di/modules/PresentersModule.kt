package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.ui.login.LoginPresenter

@Module
interface PresentersModule {

    fun mainViewModule(): LoginPresenter

}