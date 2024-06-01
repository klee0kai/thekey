package com.github.klee0kai.thekey.app.ui.storages.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.filterBy
import com.github.klee0kai.thekey.app.domain.model.sortableFlatText
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.domain.ColorGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class StoragesPresenterImpl : StoragesPresenter {

    private val rep = DI.storagesRepositoryLazy()
    private val router = DI.router()
    private val scope = DI.defaultThreadScope()

    override val searchState = MutableStateFlow(SearchState())
    override val selectedGroupId = MutableStateFlow<Long?>(null)

    private val sortedStorages = flow {
        rep().allStorages
            .map { list -> list.sortedBy { storage -> storage.sortableFlatText() } }
            .collect(this)
    }

    override val filteredColorGroups = flow {
        rep().allColorGroups.collect(this)
    }.flowOn(DI.defaultDispatcher())

    override val filteredStorages = flow {
        combine(
            flow = searchState,
            flow2 = selectedGroupId,
            flow3 = sortedStorages,
            transform = { search, selectedGroup, storages ->
                val filter = search.searchText
                var filtList = storages
                if (selectedGroup != null) filtList = filtList.filter { it.colorGroup?.id == selectedGroup }
                if (filter.isNotBlank()) filtList = filtList.filter { it.filterBy(filter) }
                filtList
            }
        ).collect(this)
    }.flowOn(DI.defaultDispatcher())

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


    override fun setColorGroup(storagePath: String, groupId: Long) = scope.launch {
        val storage = rep().findStorage(storagePath).await() ?: return@launch
        rep().setStorage(storage.copy(colorGroup = ColorGroup(id = groupId)))
    }

    override fun deleteGroup(id: Long) = scope.launch {
        rep().deleteColorGroup(id)
    }

}