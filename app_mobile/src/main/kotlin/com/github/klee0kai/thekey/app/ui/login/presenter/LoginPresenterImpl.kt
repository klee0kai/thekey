package com.github.klee0kai.thekey.app.ui.login.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.dest
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StoragesDestination
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.navigation.navigate
import com.github.klee0kai.thekey.core.utils.coroutine.coldStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginPresenterImpl(
    overridedPath: String = "",
) : LoginPresenter {

    private val scope = DI.defaultThreadScope()
    private val storagesInteractor = DI.storagesInteractorLazy()
    private val settingsRep = DI.settingsRepositoryLazy()
    private val loginInteractor = DI.loginInteractorLazy()
    private val router = DI.router()

    override val currentStorageFlow = coldStateFlow<ColoredStorage> {
        val storagePath = overridedPath.takeIf { it.isNotBlank() } ?: settingsRep().currentStoragePath()
        val storage = storagesInteractor().findStorage(storagePath, mockNew = true).await()
        result.update { storage }
    }.filterNotNull()

    override fun selectStorage() = scope.launch {
        val selectedStorage = router
            .navigate<String>(StoragesDestination)
            .firstOrNull()

        if (selectedStorage != null) {
            settingsRep()
                .currentStoragePath
                .set(selectedStorage)
        }
    }

    override fun login(passw: String) = scope.launch {
        if (passw.isBlank()) {
            router.snack(R.string.passw_is_null)
            return@launch
        }
        runCatching {
            val storage = currentStorageFlow.first()
            loginInteractor()
                .login(storage.identifier(), passw)
                .await().let {
                    router.navigate(it.dest())
                }
        }.onFailure { error ->
            Timber.d(error)
            router.snack(error.message ?: "error")
        }
    }
}
