package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.app.ui.storage.model.filterBy
import com.github.klee0kai.thekey.app.ui.storage.model.group
import com.github.klee0kai.thekey.app.ui.storage.model.sortableFlatText
import com.github.klee0kai.thekey.app.ui.storage.model.storageItem
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

open class StoragePresenterImpl(
    val storageIdentifier: StorageIdentifier,
) : StoragePresenter {
    private val notesInteractor = DI.notesInteractorLazy(storageIdentifier)
    private val otpNotesInteractor = DI.otpNotesInteractorLazy(storageIdentifier)
    private val groupsInteractor = DI.groupsInteractorLazy(storageIdentifier)

    private val scope = DI.defaultThreadScope()

    override val searchState = MutableStateFlow(SearchState())
    override val selectedGroupId = MutableStateFlow<Long?>(null)

    private val sortedStorageItems = flow<List<StorageItem>> {
        combine(
            flow = notesInteractor().notes,
            flow2 = otpNotesInteractor().otpNotes,
            transform = { notes, otpNotes ->
                notes.map { it.storageItem() } + otpNotes.map { it.storageItem() }
                    .sortedBy { it.sortableFlatText() }
            }).collect(this)
    }

    override val filteredColorGroups = flow<List<ColorGroup>> {
        groupsInteractor().groups.collect(this@flow)
    }

    override val filteredItems = flow<List<StorageItem>> {
        combine(
            flow = searchState,
            flow2 = selectedGroupId,
            flow3 = sortedStorageItems,
            transform = { search, selectedGroup, items ->
                val filter = search.searchText
                var filtList = items
                if (selectedGroup != null) filtList =
                    filtList.filter { it.group.id == selectedGroup }
                if (filter.isNotBlank()) filtList = filtList.filter { it.filterBy(filter) }
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
        val oldNoteGroupId = notesInteractor().notes
            .firstOrNull()
            ?.firstOrNull { it.ptnote == notePt }
            ?.group
            ?.id
            ?: return@launch
        if (oldNoteGroupId ==groupId){
            notesInteractor().setNotesGroup(listOf(notePt), 0)
        }else {
            notesInteractor().setNotesGroup(listOf(notePt), groupId)
        }
    }

    override fun deleteGroup(id: Long) = scope.launch {
        groupsInteractor().removeGroup(id)
    }

}