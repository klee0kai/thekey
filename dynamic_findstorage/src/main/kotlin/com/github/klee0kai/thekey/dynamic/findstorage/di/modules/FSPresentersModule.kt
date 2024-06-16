package com.github.klee0kai.thekey.dynamic.findstorage.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenter
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.presenter.FSStoragesPresenter
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.presenter.FSStoragesPresenterImpl

@Module
interface FSPresentersModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun fsStoragesPresenter(
        origin: StoragesPresenter,
    ): FSStoragesPresenter = FSStoragesPresenterImpl(origin = origin)

}