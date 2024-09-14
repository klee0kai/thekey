package com.github.klee0kai.thekey.app.ui.selectstorageboard.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SelectStorageToNoteMoveBoardPresenterImpl : SelectStorageToNoteMoveBoardPresenter {

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
    }.flowOn(DI.defaultDispatcher())

    override val openedStoragesFlow = flow<List<ColoredStorage>> {
        loginInteractor().authorizedStorages.collect(this)
    }.flowOn(DI.defaultDispatcher())

    override fun select(
        storagePath: String,
        router: AppRouter?,
    ) = scope.launch {

    }


}