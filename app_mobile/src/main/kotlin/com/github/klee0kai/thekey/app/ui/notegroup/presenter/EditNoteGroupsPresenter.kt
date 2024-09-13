package com.github.klee0kai.thekey.app.ui.notegroup.presenter

import com.github.klee0kai.thekey.app.ui.notegroup.model.EditNoteGroupsState
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface EditNoteGroupsPresenter {

    val searchState: Flow<SearchState> get() = emptyFlow()

    val state: Flow<EditNoteGroupsState> get() = emptyFlow()

    val filteredItems: Flow<List<StorageItem>> get() = emptyFlow()

    fun init(): Job = emptyJob()

    fun searchFilter(newParams: SearchState): Job = emptyJob()

    fun input(block: EditNoteGroupsState.() -> EditNoteGroupsState): Job = emptyJob()

    fun save(router: AppRouter?): Job = emptyJob()

    fun remove(router: AppRouter?): Job = emptyJob()

}

fun EditNoteGroupsPresenter.selectStorageItem(id: String, selected: Boolean) = input {
    copy(
        selectedStorageItems = if (!selected) {
            selectedStorageItems.toMutableSet().apply { remove(id) }
        } else {
            selectedStorageItems + setOf(id)
        }
    )
}