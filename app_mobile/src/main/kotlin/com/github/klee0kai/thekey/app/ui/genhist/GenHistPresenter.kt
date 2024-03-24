package com.github.klee0kai.thekey.app.ui.genhist

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.ui.genhist.model.LazyPassw
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GenHistPresenter(
    val storageIdentifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val navigator = DI.router()
    private val engine = DI.cryptStorageEngineLazy(storageIdentifier)

    fun histNoteProviders(): Deferred<List<LazyPassw>> = scope.async {
        val engine = engine() ?: return@async emptyList()
        engine.genHistory()
            .reversed()
            .map {
                LazyPassw(it) {
                    withContext(DI.defaultDispatcher()) {
                        engine.getGenPassw(it.passwPtr)
                    }
                }
            }
    }

}