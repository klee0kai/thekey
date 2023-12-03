package com.github.klee0kai.thekey.app.ui.storages

import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class StoragesPresenter {

    private val interactor by DI.findStoragesInteractorLazy()
    private val navigator = DI.navigator()
    private val scope = DI.mainThreadScope()

    fun storages(filter: String = "") =
        interactor.storagesFlow
            .flowOn(Dispatchers.Default)
            .map {
                it.filter { storage ->
                    filter.isBlank() || storage.path.contains(filter)
                }
            }


}