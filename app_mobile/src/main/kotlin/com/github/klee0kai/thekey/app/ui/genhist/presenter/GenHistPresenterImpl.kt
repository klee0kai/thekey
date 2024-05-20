package com.github.klee0kai.thekey.app.ui.genhist.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.HistPassw
import com.github.klee0kai.thekey.app.domain.model.histPasww
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

open class GenHistPresenterImpl(
    val storageIdentifier: StorageIdentifier,
) : GenHistPresenter {

    private val scope = DI.defaultThreadScope()
    private val router = DI.router()
    private val engine = DI.cryptStorageEngineSafeLazy(storageIdentifier)

    override val histFlow = flow<List<HistPassw>> {
        engine().genHistory()
            .reversed()
            .map { it.histPasww() }
            .also { emit(it) }

        engine().genHistory(info = true)
            .reversed()
            .map { it.histPasww(isLoaded = true) }
            .also { emit(it) }
    }.flowOn(DI.defaultDispatcher())

}