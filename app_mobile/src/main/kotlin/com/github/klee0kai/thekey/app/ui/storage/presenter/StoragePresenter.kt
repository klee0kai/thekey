package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface StoragePresenter {

    val searchState: Flow<SearchState>
        get() = MutableStateFlow(SearchState())

    val selectedGroupId: Flow<Long?>
        get() = MutableStateFlow(null)

    val filteredColorGroups: Flow<List<ColorGroup>>
        get() = MutableStateFlow(emptyList())

    val filteredItems: Flow<List<StorageItem>>
        get() = MutableStateFlow(emptyList())

    fun searchFilter(newParams: SearchState): Job = Job()

    fun selectGroup(groupId: Long): Job = Job()

    fun setColorGroup(notePt: Long, groupId: Long): Job = Job()

    fun deleteGroup(id: Long): Job = Job()

}