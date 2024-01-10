package com.github.klee0kai.thekey.app.ui.login

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.StoragesDestination
import com.github.klee0kai.thekey.app.ui.navigation.navigateForResult
import com.github.klee0kai.thekey.app.ui.navigation.snack
import com.github.klee0kai.thekey.app.utils.coroutine.asyncResult
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class LoginPresenter {

    private val storagesRep = DI.foundStoragesRepositoryLazy()
    private val settingsRep = DI.settingsRepositoryLazy()
    private val navigator = DI.navigator()
    private val scope = DI.mainThreadScope()

    fun currentStorageFlow() = flow<ColoredStorage> {
        val storagePath = settingsRep().currentStoragePath.get().await()
        val storage = storagesRep().findStorage(storagePath).await()
            ?: ColoredStorage(path = storagePath)
        emit(storage)
    }

    fun selectStorage() = scope.launch {
        val selectedStorage = navigator
            .navigateForResult<String>(StoragesDestination)
            .firstOrNull()

        if (selectedStorage != null) {
            settingsRep()
                .currentStoragePath
                .set(selectedStorage)
        }
    }

    fun login(passw: String) = scope.asyncResult {
        if (passw.isBlank()) {
            navigator.snack(R.string.passw_is_null)
            return@asyncResult
        }
        val storage = currentStorageFlow().first()
        val engine = DI.cryptStorageEngineLazy(StorageIdentifier(storage.path))
        engine().login(passw)
        navigator.navigate(StorageDestination(path = storage.path))
    }

}