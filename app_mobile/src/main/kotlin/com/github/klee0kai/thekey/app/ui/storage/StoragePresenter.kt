package com.github.klee0kai.thekey.app.ui.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.model.LazyNote
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.utils.common.fromPreloadedOrCreate
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoragePresenter(
    val storageIdentifier: StorageIdentifier,
) {
    private val engine = DI.cryptStorageEngineSafeLazy(storageIdentifier)
    private val router = DI.router()
    private val scope = DI.defaultThreadScope()

    val searchState = MutableStateFlow(SearchState())

    private var allNotes = emptyList<LazyNote>()
    val filteredNotes = MutableStateFlow<List<LazyNote>>(emptyList())

    fun collectNotesFromEngine(forceDirty: Boolean = false) = scope.launchLatest("collect_from_engine") {
        val engine = engine() ?: return@launchLatest
        val oldNotes = allNotes
        allNotes = engine
            .notes()
            .map { noteLite ->
                fromPreloadedOrCreate(noteLite.ptnote, oldNotes) {
                    withContext(DI.defaultDispatcher()) {
                        engine.note(noteLite.ptnote)
                    }
                }.apply {
                    dirty = forceDirty
                }
            }

        filterNotes()
    }

    fun filterNotes(newParams: SearchState? = null) = scope.launchLatest("filter") {
        if (newParams != null) searchState.value = newParams
        val filter = searchState.value.searchText
        val notes = allNotes

        if (filter.isBlank()) {
            filteredNotes.emit(notes)
            return@launchLatest
        }
        notes.map { it.fullValueFlow() }
            .merge()
            .collect {
                notes.filter {
                    val note = it.getOrNull() ?: return@filter false
                    note.site.contains(filter, ignoreCase = true)
                            || note.login.contains(filter, ignoreCase = true)
                            || note.desc.contains(filter, ignoreCase = true)
                }.also { filteredNotes.emit(it) }
            }
    }

    fun remove(notePt: Long) = scope.launch {
        engine()?.removeNote(notePt)

        collectNotesFromEngine()
    }


    fun doLogout() = scope.launch { engine()?.unlogin() }

}