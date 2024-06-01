package com.github.klee0kai.thekey.app.ui.storages.presenter

import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.domain.ColorGroup
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface StoragesPresenter {

    val searchState: Flow<SearchState>
        get() = MutableStateFlow(SearchState())

    val selectedGroupId: Flow<Long?>
        get() = MutableStateFlow(null)

    val filteredColorGroups: Flow<List<ColorGroup>>
        get() = MutableStateFlow(emptyList())

    val filteredStorages: Flow<List<ColoredStorage>>
        get() = MutableStateFlow(emptyList())

    fun init(): Job = emptyJob()

    fun searchFilter(newParams: SearchState): Job = emptyJob()

    fun selectGroup(groupId: Long): Job = emptyJob()

    fun setColorGroup(storagePath: String, groupId: Long): Job = emptyJob()

    fun deleteGroup(id: Long): Job = emptyJob()

}