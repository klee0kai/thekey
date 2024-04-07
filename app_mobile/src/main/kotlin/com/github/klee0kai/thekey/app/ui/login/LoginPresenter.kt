package com.github.klee0kai.thekey.app.ui.login

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.dest
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.StoragesDestination
import com.github.klee0kai.thekey.app.ui.navigation.navigate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginPresenter {

    private val scope = DI.defaultThreadScope()
    private val storagesRep = DI.foundStoragesRepositoryLazy()
    private val settingsRep = DI.settingsRepositoryLazy()
    private val loginInteractor = DI.loginInteractorLazy()
    private val router = DI.router()

    fun currentStorageFlow() = flow<ColoredStorage> {
        val storagePath = settingsRep().currentStoragePath()
        val newStorageVers = settingsRep().newStorageVersion()
        val storage = storagesRep().findStorage(storagePath).await()
            ?: ColoredStorage(version = newStorageVers, path = storagePath)
        emit(storage)
    }

    fun selectStorage() = scope.launch {
        val selectedStorage = router
            .navigate<String>(StoragesDestination)
            .firstOrNull()

        if (selectedStorage != null) {
            settingsRep()
                .currentStoragePath
                .set(selectedStorage)
        }
    }

    fun login(passw: String) = scope.launch {
        if (passw.isBlank()) {
            router.snack(R.string.passw_is_null)
            return@launch
        }
        runCatching {
            val storage = currentStorageFlow().first()
            loginInteractor()
                .login(storage.identifier(), passw)
                .await()
            router.navigate(storage.dest())
        }.onFailure { error ->
            Timber.d(error)
            router.snack(error.message ?: "error")
        }
    }

}

