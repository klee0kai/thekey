package com.github.klee0kai.thekey.app.ui.login

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.StoragesDestination
import com.github.klee0kai.thekey.app.ui.navigation.navigate
import com.github.klee0kai.thekey.app.utils.coroutine.asyncResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class LoginPresenter {

    private val storagesRep = DI.foundStoragesRepositoryLazy()
    private val settingsRep = DI.settingsRepositoryLazy()
    private val router = DI.router()
    private val scope = DI.mainThreadScope()

    fun currentStorageFlow() = flow<ColoredStorage> {
        val storagePath = settingsRep().currentStoragePath.get().await()
        val storage = storagesRep().findStorage(storagePath).await()
            ?: ColoredStorage(path = storagePath)
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

    fun login(passw: String) = scope.asyncResult {
        if (passw.isBlank()) {
//            router.snack(R.string.passw_is_null)
            return@asyncResult
        }
        val storage = currentStorageFlow().first()
        val engine = DI.cryptStorageEngineLazy(StorageIdentifier(storage.path))
        engine().login(passw)
        router.navigate(StorageDestination(path = storage.path))
    }

}