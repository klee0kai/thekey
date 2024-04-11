package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.domain.model.ColoredNote
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

open class StoragePresenterImpl(
    val storageIdentifier: StorageIdentifier,
) : StoragePresenter {
    private val notesInteractor = DI.notesInteractorLazy(storageIdentifier)
    private val groupsInteractor = DI.groupsInteractorLazy(storageIdentifier)

    private val router = DI.router()
    private val scope = DI.defaultThreadScope()

    override val searchState = MutableStateFlow(SearchState())
    override val selectedGroupId = MutableStateFlow<Long?>(null)

    override val filteredColorGroups = flow<List<ColorGroup>> {
        groupsInteractor().groups.collect(this@flow)
    }

    override val filteredNotes = flow<List<ColoredNote>> {
        combine(
            flow = searchState,
            flow2 = selectedGroupId,
            flow3 = notesInteractor().notes,
            transform = { search, selectedGroup, orList ->
                val filter = search.searchText
                var filtList = orList

                if (selectedGroup != null) {
                    filtList = filtList.filter {
                        it.group.id == selectedGroup
                    }
                }

                if (filter.isNotBlank()) {
                    filtList = filtList.filter { note ->
                        note.site.contains(filter, ignoreCase = true)
                                || note.login.contains(filter, ignoreCase = true)
                                || note.desc.contains(filter, ignoreCase = true)
                    }
                }
                Timber.d("StoragePresenterImpl notes ${filtList}")
                filtList
            }
        ).collect(this@flow)
    }

    override fun searchFilter(newParams: SearchState) = scope.launch {
        searchState.value = newParams
    }

    override fun selectGroup(groupId: Long) = scope.launch {
        if (selectedGroupId.value == groupId) {
            selectedGroupId.value = null
        } else {
            selectedGroupId.value = groupId
        }
    }

    override fun setColorGroup(notePt: Long, groupId: Long) = scope.launch {
        notesInteractor().setNotesGroup(listOf(notePt), groupId)
    }

    override fun deleteGroup(id: Long) = scope.launch {
        groupsInteractor().removeGroup(id)
    }

}