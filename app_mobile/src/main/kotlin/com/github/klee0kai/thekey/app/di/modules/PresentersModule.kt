package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.ui.editstorage.CreateStoragePresenter
import com.github.klee0kai.thekey.app.ui.editstorage.EditStoragePresenter
import com.github.klee0kai.thekey.app.ui.login.LoginPresenter
import com.github.klee0kai.thekey.app.ui.storages.StoragesPresenter

@Module
abstract class PresentersModule {

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun loginPresenter(): LoginPresenter

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun storagesPresenter(): StoragesPresenter

    @Provide(cache = Provide.CacheType.Weak)
    open fun editStoragePresenter(storageIdentifier: StorageIdentifier): CreateStoragePresenter {
        return if (storageIdentifier.path == null) {
            CreateStoragePresenter()
        } else {
            EditStoragePresenter(storageIdentifier.path)
        }
    }

}