package com.github.klee0kai.thekey.app.ui.genhist

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.model.LazyPassw
import com.github.klee0kai.thekey.app.utils.common.preloaded
import com.github.klee0kai.thekey.app.utils.common.singleEventFlow
import kotlinx.coroutines.withContext

class GenHistPresenter(
    val storageIdentifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val navigator = DI.router()
    private val engine = DI.cryptStorageEngineLazy(storageIdentifier)

    private var lazyHist = emptyList<LazyPassw>()

    fun hist() = singleEventFlow<List<LazyPassw>>(DI.defaultDispatcher()) {
        val engine = engine() ?: return@singleEventFlow emptyList()
        engine.genHistory()
            .reversed()
            .map {
                LazyPassw(it) {
                    withContext(DI.defaultDispatcher()) {
                        engine.getGenPassw(it.passwPtr)
                    }
                }
            }.preloaded(lazyHist)
            .also { lazyHist = it }
    }

}