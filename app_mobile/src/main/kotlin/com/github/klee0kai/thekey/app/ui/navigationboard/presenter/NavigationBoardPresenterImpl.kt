package com.github.klee0kai.thekey.app.ui.navigationboard.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class NavigationBoardPresenterImpl : NavigationBoardPresenter {

    private val scope = DI.defaultThreadScope()
    private val router = DI.router()
    private val loginInteractor = DI.loginInteractorLazy()
    private val foundStoragesRep = DI.storagesRepositoryLazy()

    override val currentStorage = flow<ColoredStorage?> {
        router.navChanges
            .map { backChange ->
                val storageDest = backChange.currentNavStack
                    .lastOrNull { it.destination is StorageDestination }
                    ?.destination as? StorageDestination

                if (storageDest != null) {
                    foundStoragesRep().findStorage(storageDest.path).await()
                        ?: ColoredStorage(path = storageDest.path)
                } else {
                    null
                }
            }.collect(this)
    }

    override val openedStoragesFlow = flow<List<ColoredStorage>> {
        loginInteractor().logginedStorages.collect(this)
    }

}