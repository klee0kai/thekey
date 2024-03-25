package com.github.klee0kai.thekey.app.ui.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.model.LazyNote
import com.github.klee0kai.thekey.app.utils.common.preloaded
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoragePresenter(
    val storageIdentifier: StorageIdentifier,
) {
    private val engine = DI.cryptStorageEngineLazy(storageIdentifier)
    private val router = DI.router()
    private val scope = DI.defaultThreadScope()
    private val updateTicks = MutableSharedFlow<Unit>()

    var notes = emptyList<LazyNote>()

    fun notes() = flow<List<LazyNote>> {
        val engine = engine() ?: return@flow
        val collectNotes: suspend () -> Unit = {
            notes = engine
                .notes()
                .map {
                    LazyNote(it) {
                        withContext(DI.defaultDispatcher()) {
                            engine.note(it.ptnote)
                        }
                    }
                }
                .preloaded(notes)
            emit(notes)
        }
        collectNotes.invoke()
        updateTicks.collect {
            collectNotes.invoke()
        }
    }.flowOn(DI.defaultDispatcher())

    fun remove(notePt: Long) = scope.launch {
        engine()?.removeNote(notePt)
        updateTicks.emit(Unit)
    }

    private fun doLogout() = scope.launch { engine()?.unlogin() }


}