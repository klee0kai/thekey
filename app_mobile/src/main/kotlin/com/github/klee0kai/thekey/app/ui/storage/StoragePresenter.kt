package com.github.klee0kai.thekey.app.ui.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.navigation.StorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.awaitScreenEvent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class StoragePresenter(
    val storagePath: String = "",
) {
    private val engine = DI.cryptStorageEngineLazy(StorageIdentifier(storagePath))
    private val navigator = DI.navigator()
    private val scope = DI.mainThreadScope()

    init {
        scope.launch {
            navigator.awaitScreenEvent(StorageDestination(storagePath))
            doLogout()
        }
    }


    fun notes(): Deferred<List<DecryptedNote>> = scope.async {
        engine().notes().toList()
    }

    private fun doLogout() = scope.launch { engine().unlogin() }
    

}