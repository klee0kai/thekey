package com.github.klee0kai.thekey.app.ui.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.model.LazyNote
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.utils.common.launchLatestSafe
import com.github.klee0kai.thekey.app.utils.common.preloaded
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoragePresenter(
    val storageIdentifier: StorageIdentifier,
) {
    private val engine = DI.cryptStorageEngineLazy(storageIdentifier)
    private val router = DI.router()
    private val scope = DI.defaultThreadScope()
    private val updateTicks = MutableSharedFlow<Unit>()

    private var lazyNotes = emptyList<LazyNote>()

    val searchState = MutableStateFlow(SearchState())

    fun init() {
        collectNotesFromEngine()
    }

    fun notes() = flow<List<LazyNote>> {
        filtered(searchState.value.searchText)
            .collect(this)

        merge(searchState, updateTicks).collect {
            filtered(searchState.value.searchText)
                .collect(this)
        }
    }.flowOn(DI.defaultDispatcher())

    fun remove(notePt: Long) = scope.launch {
        engine()?.removeNote(notePt)

        collectNotesFromEngine()
    }

    private fun doLogout() = scope.launch { engine()?.unlogin() }

    private fun filtered(filter: String) = flow {
        if (filter.isBlank()) {
            emit(lazyNotes)
            return@flow
        }

        val notes = lazyNotes
        notes.map { it.fullValueFlow() }
            .merge()
            .collect {
                notes.filter {
                    val note = it.getOrNull() ?: return@filter false
                    note.site.contains(filter, ignoreCase = true)
                            || note.login.contains(filter, ignoreCase = true)
                            || note.desc.contains(filter, ignoreCase = true)
                }.also { emit(it) }
            }
    }

    private fun collectNotesFromEngine() = scope.launchLatestSafe("collect_from_engine") {
        val engine = engine() ?: return@launchLatestSafe
        lazyNotes = engine
            .notes()
            .map {
                LazyNote(it) {
                    withContext(DI.defaultDispatcher()) {
                        engine.note(it.ptnote)
                    }
                }
            }
            .preloaded(lazyNotes)

        updateTicks.emit(Unit)
    }


}