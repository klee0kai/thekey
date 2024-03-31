package com.github.klee0kai.thekey.app.ui.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.colorGroup
import com.github.klee0kai.thekey.app.model.ColorGroup
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
    val selectedGroupId = MutableStateFlow<Long?>(null)

    private var allGroups = emptyList<ColorGroup>()
    val filteredColorGroups = MutableStateFlow<List<ColorGroup>>(emptyList())

    private var allNotes = emptyList<LazyNote>()
    val filteredNotes = MutableStateFlow<List<LazyNote>>(emptyList())

    fun collectGroupsFromEngine() = scope.launchLatest("collect_groups") {
        val engine = engine() ?: return@launchLatest

        allGroups = engine
            .colorGroups()
            .map { it.colorGroup() }
        filteredColorGroups.value = allGroups

        allGroups = engine
            .colorGroups(info = true)
            .map { it.colorGroup() }
        filteredColorGroups.value = allGroups
    }

    fun collectNotesFromEngine(forceDirty: Boolean = false) = scope.launchLatest("collect_notes") {
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

    fun searchFilter(newParams: SearchState) = scope.launch {
        searchState.value = newParams
        filterNotes()
    }

    fun selectGroup(groupId: Long) = scope.launch {
        if (selectedGroupId.value == groupId) {
            selectedGroupId.value = null
        } else {
            selectedGroupId.value = groupId
        }
        filterNotes()
    }

    private fun filterNotes() = scope.launchLatest("filter") {
        val selectedGroup = selectedGroupId.value
        val filter = searchState.value.searchText
        val notes = allNotes

        notes.map { it.fullValueFlow() }
            .merge()
            .collect {
                var filtList = notes
                if (selectedGroup != null) {
                    filtList = filtList.filter {
                        it.getOrNull()?.colorGroupId == selectedGroup
                    }
                }

                if (filter.isNotBlank()) {
                    filtList = filtList.filter {
                        val note = it.getOrNull() ?: return@filter false
                        note.site.contains(filter, ignoreCase = true)
                                || note.login.contains(filter, ignoreCase = true)
                                || note.desc.contains(filter, ignoreCase = true)
                    }
                }

                filteredNotes.emit(filtList)
            }
    }

    fun remove(notePt: Long) = scope.launch {
        engine()?.removeNote(notePt)

        collectNotesFromEngine()
    }


    fun doLogout() = scope.launch { engine()?.unlogin() }

}