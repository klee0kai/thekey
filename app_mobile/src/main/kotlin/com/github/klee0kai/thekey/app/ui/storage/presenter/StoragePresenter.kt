package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.domain.model.LazyColorGroup
import com.github.klee0kai.thekey.app.domain.model.LazyColoredNote
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface StoragePresenter {

    val searchState: Flow<SearchState>
        get() = MutableStateFlow(SearchState())

    val selectedGroupId: Flow<Long?>
        get() = MutableStateFlow(null)

    val filteredColorGroups: Flow<List<LazyColorGroup>>
        get() = MutableStateFlow(emptyList())

    val filteredNotes: Flow<List<LazyColoredNote>>
        get() = MutableStateFlow(emptyList())

    fun searchFilter(newParams: SearchState): Job = Job()

    fun selectGroup(groupId: Long): Job = Job()

    fun setColorGroup(notePt: Long, groupId: Long): Job = Job()

    fun deleteGroup(id: Long): Job = Job()

}