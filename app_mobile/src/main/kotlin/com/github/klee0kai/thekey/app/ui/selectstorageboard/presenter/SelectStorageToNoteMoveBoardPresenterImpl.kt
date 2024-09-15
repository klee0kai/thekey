package com.github.klee0kai.thekey.app.ui.selectstorageboard.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.BackType
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
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

    override val selectableOpenStorages = flow<List<ColoredStorage>> {
        combine(
            currentStorage,
            loginInteractor().authorizedStorages,
        ) { current, openned -> openned.filter { it.path != current?.path } }
            .collect(this)
    }.flowOn(DI.defaultDispatcher())

    override fun select(
        storagePath: String,
        router: AppRouter?,
    ) = scope.launch {
        router?.hideNavigationBoard()
        val storages = selectableOpenStorages.firstOrNull() ?: return@launch
        val selectedStorage = storages.firstOrNull { it.path == storagePath } ?: return@launch
        router?.backWithResult(selectedStorage, BackType.BoardOnly)
    }


}