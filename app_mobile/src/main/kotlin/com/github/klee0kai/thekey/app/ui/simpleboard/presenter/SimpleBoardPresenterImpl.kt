package com.github.klee0kai.thekey.app.ui.simpleboard.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.dest
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.StorageDestination
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.feature.PaidFeature
import com.github.klee0kai.thekey.core.domain.model.feature.PaidLimits
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.coroutine.lazyStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class SimpleBoardPresenterImpl : SimpleBoardPresenter {

    private val scope = DI.defaultThreadScope()
    private val router = DI.router()
    private val loginInteractor = DI.loginInteractorLazy()
    private val foundStoragesRep = DI.storagesRepositoryLazy()
    private val storagesInteractor = DI.storagesInteractorLazy()
    private val billing = DI.billingInteractor()

    override val currentStorage = lazyStateFlow(
        init = null as? ColoredStorage?,
        defaultArg = Unit,
        scope = scope,
    ) {
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

    override val openedStoragesFlow = lazyStateFlow(
        init = emptyList<ColoredStorage>(),
        defaultArg = Unit,
        scope = scope,
    ) { loginInteractor().authorizedStorages.collect(this) }

    override val favoritesStorages: Flow<List<ColoredStorage>> = flow {
        storagesInteractor().allStorages.map { list ->
            var storages = list.filter { storage -> storage.colorGroup?.isFavorite ?: false }
            if (!billing.isAvailable(PaidFeature.UNLIMITED_FAVORITE_STORAGES))
                storages = storages.take(PaidLimits.PAID_FAVORITE_STORAGE_LIMITS)
            storages
        }.collect(this)
    }.flowOn(DI.defaultDispatcher())

    override fun openStorage(storagePath: String, router: AppRouter?) = scope.launch {
        val currentLogined = currentStorage.firstOrNull()
        val openedStorage = openedStoragesFlow.firstOrNull()?.firstOrNull { it.path == storagePath }
        router?.hideNavigationBoard()
        if (currentLogined?.path == storagePath) return@launch
        if (openedStorage != null) {
            router?.resetStack(LoginDestination(), openedStorage.dest())
        } else {
            val storage = storagesInteractor()
                .findStorage(storagePath)
                .await()
                ?: return@launch

            router?.resetStack(
                LoginDestination(
                    identifier = storage.identifier(),
                    forceAllowStorageSelect = true,
                )
            )
        }
    }

    override fun logout(storagePath: String, router: AppRouter?) = scope.launch {
        val currentLogined = currentStorage.firstOrNull()
        router?.hideNavigationBoard()
        if (currentLogined?.path == storagePath) router?.resetStack(LoginDestination())
        val storage = storagesInteractor().findStorage(storagePath).await() ?: return@launch
        loginInteractor().logout(storage.identifier()).join()
    }

}