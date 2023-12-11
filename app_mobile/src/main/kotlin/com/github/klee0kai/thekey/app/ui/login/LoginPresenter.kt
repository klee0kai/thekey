package com.github.klee0kai.thekey.app.ui.login

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.data.SettingsRepository.Companion.SETTING_DEFAULT_STORAGE_PATH
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.Destination
import com.github.klee0kai.thekey.app.utils.coroutine.asyncResult
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.IOException

class LoginPresenter {

    private val storagesRep = DI.foundStoragesRepositoryLazy()
    private val settingsRep = DI.settingsRepositoryLazy()
    private val navigator = DI.navigator()
    private val scope = DI.mainThreadScope()

    fun currentStorageFlow() = flow<ColoredStorage> {
        val storagePath = settingsRep().get(SETTING_DEFAULT_STORAGE_PATH)
            .await() ?: File(DI.app().applicationInfo?.dataDir, "keys.ckey").path
        val storage = storagesRep().findStorage(storagePath).await()
            ?: ColoredStorage(path = storagePath)
        emit(storage)
    }

    fun login(passw: String) = scope.asyncResult {
        if (passw.isBlank()) throw IOException(DI.app().getString(R.string.passw_is_null))
        val storage = currentStorageFlow().first()
        val engine = DI.cryptStorageEngineLazy(StorageIdentifier(storage.path))
        engine().login(passw)
        navigator.navigate(Destination.StorageScreen(path = storage.path))
    }

}