package com.github.klee0kai.thekey.app.ui.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.navigation.toDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class StoragePresenter(
    val storageIdentifier: StorageIdentifier,
) {
    private val engine = DI.cryptStorageEngineLazy(storageIdentifier)
    private val router = DI.router()
    private val scope = DI.mainThreadScope()
    private val updateTicks = MutableSharedFlow<Unit>()

    init {
        scope.launch {
            router.awaitScreenEvent(storageIdentifier.toDestination())
            doLogout()
        }
    }


    fun notes(): Flow<List<DecryptedNote>> = flow {
        emit(engine().notes().toList())
        updateTicks.collect {
            emit(engine().notes().toList())
        }
    }.flowOn(DI.defaultDispatcher())

    fun remove(notePt: Long) = scope.launch {
        engine().removeNote(notePt)
        updateTicks.emit(Unit)
    }

    private fun doLogout() = scope.launch { engine().unlogin() }


}