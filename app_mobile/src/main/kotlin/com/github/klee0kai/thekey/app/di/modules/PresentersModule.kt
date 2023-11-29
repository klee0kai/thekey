package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.ui.login.LoginPresenter
import com.github.klee0kai.thekey.app.ui.storages.StoragesPresenter

@Module
interface PresentersModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun mainPresenter(): LoginPresenter

    @Provide(cache = Provide.CacheType.Weak)
    fun storagesPresenter(): StoragesPresenter

}