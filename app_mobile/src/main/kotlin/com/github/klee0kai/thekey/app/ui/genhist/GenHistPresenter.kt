package com.github.klee0kai.thekey.app.ui.genhist

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.model.LazyPassw
import com.github.klee0kai.thekey.app.model.id
import com.github.klee0kai.thekey.app.utils.common.singleEventFlow
import com.github.klee0kai.thekey.app.utils.lazymodel.fromPreloadedOrCreate
import kotlinx.coroutines.withContext

class GenHistPresenter(
    val storageIdentifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val navigator = DI.router()
    private val engine = DI.cryptStorageEngineSafeLazy(storageIdentifier)

    private var lazyHist = emptyList<LazyPassw>()

    fun hist() = singleEventFlow<List<LazyPassw>>(DI.defaultDispatcher()) {
        engine().genHistory()
            .reversed()
            .map { passwLite ->
                fromPreloadedOrCreate(passwLite.passwPtr, lazyHist) {
                    withContext(DI.defaultDispatcher()) {
                        engine().getGenPassw(id)
                    }
                }.apply {
                    dirty()
                }
            }
            .also { lazyHist = it }
    }

}