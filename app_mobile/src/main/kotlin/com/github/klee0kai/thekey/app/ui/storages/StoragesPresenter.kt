package com.github.klee0kai.thekey.app.ui.storages

import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI

class StoragesPresenter {

    private val rep by DI.storagesRepositoryLazy()
    private val navigator = DI.navigator()
    private val scope = DI.mainThreadScope()


}