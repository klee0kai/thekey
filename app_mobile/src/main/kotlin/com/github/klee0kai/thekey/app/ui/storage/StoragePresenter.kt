package com.github.klee0kai.thekey.app.ui.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.domain.model.LazyColorGroup
import com.github.klee0kai.thekey.app.domain.model.LazyColoredNote
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class StoragePresenter(
    val storageIdentifier: StorageIdentifier,
) {
    private val notesInteractor = DI.notesInteractorLazy(storageIdentifier)
    private val groupsInteractor = DI.groupsInteractorLazy(storageIdentifier)

    private val router = DI.router()
    private val scope = DI.defaultThreadScope()

    val searchState = MutableStateFlow(SearchState())
    val selectedGroupId = MutableStateFlow<Long?>(null)

    val filteredColorGroups = flow<List<LazyColorGroup>> {
        groupsInteractor().groups.collect(this@flow)
    }

    val filteredNotes = flow<List<LazyColoredNote>> {
        combine(
            flow = searchState,
            flow2 = selectedGroupId,
            flow3 = notesInteractor().notes,
            transform = { search, selectedGroup, orList ->
                val filter = search.searchText
                var filtList = orList

                if (selectedGroup != null) {
                    filtList = filtList.filter {
                        it.getOrNull()?.group?.id == selectedGroup
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
                filtList
            }
        ).collect(this@flow)
    }

    fun searchFilter(newParams: SearchState) = scope.launch {
        searchState.value = newParams
    }

    fun selectGroup(groupId: Long) = scope.launch {
        if (selectedGroupId.value == groupId) {
            selectedGroupId.value = null
        } else {
            selectedGroupId.value = groupId
        }
    }

    fun setColorGroup(notePt: Long, groupId: Long) = scope.launch {
        notesInteractor().setNoteGroup(notePt, groupId)
    }

}