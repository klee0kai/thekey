package com.github.klee0kai.thekey.app.ui.login.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.dest
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StoragesDestination
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.navigate
import com.github.klee0kai.thekey.core.utils.common.launchDebounced
import com.github.klee0kai.thekey.core.utils.coroutine.coldStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import timber.log.Timber

class LoginPresenterImpl(
    private val overrided: StorageIdentifier = StorageIdentifier(),
) : LoginPresenter {

    private val scope = DI.defaultThreadScope()
    private val storagesInteractor = DI.storagesInteractorLazy()
    private val settingsRep = DI.settingsRepositoryLazy()
    private val loginInteractor = DI.loginInteractorLazy()

    override val currentStorageFlow = coldStateFlow<ColoredStorage> {
        val storagePath =
            overrided.path.takeIf { it.isNotBlank() } ?: settingsRep().currentStoragePath()
        val storage = storagesInteractor().findStorage(storagePath, mockNew = true).await()
        result.update { storage }
    }.filterNotNull()

    override fun selectStorage(router: AppRouter?) = scope.launchDebounced("select_storage") {
        val selectedStorage = router
            ?.navigate<String>(StoragesDestination)
            ?.firstOrNull()

        if (selectedStorage != null) {
            settingsRep()
                .currentStoragePath
                .set(selectedStorage)
        }
    }

    override fun login(passw: String, router: AppRouter?) = scope.launchDebounced("login") {
        if (passw.isBlank()) {
            router?.snack(R.string.passw_is_null)
            return@launchDebounced
        }
        runCatching {
            val storageIdentifier = if (overrided.fileDescriptor != null) {
                overrided
            } else {
                currentStorageFlow.first().identifier()
            }

            loginInteractor()
                .login(storageIdentifier, passw)
                .await().let {
                    router?.navigate(it.dest())
                }
        }.onFailure { error ->
            Timber.d(error)
            router?.snack(error.message ?: "error")
        }
    }
}
