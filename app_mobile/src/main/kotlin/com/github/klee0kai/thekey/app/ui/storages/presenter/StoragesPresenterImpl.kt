package com.github.klee0kai.thekey.app.ui.storages.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.domain.model.filterBy
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.domain.ColorGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class StoragesPresenterImpl : StoragesPresenter {

    private val interactor = DI.findStoragesInteractorLazy()
    private val rep = DI.foundStoragesRepositoryLazy()
    private val router = DI.router()
    private val scope = DI.defaultThreadScope()

    override val searchState = MutableStateFlow(SearchState())
    override val selectedGroupId = MutableStateFlow<Long?>(null)

    private val sortedStorages = flow {
        interactor().storagesFlow
            .flowOn(Dispatchers.Default)
            .collect(this)
    }

    override val filteredColorGroups = flow<List<ColorGroup>> {
        rep()
            .updateDbFlow
            .map {
                rep().getAllColorGroups().await()
            }.collect(this@flow)
    }

    override val filteredStorages = flow<List<ColoredStorage>> {
        combine(
            flow = searchState,
            flow2 = selectedGroupId,
            flow3 = sortedStorages,
            transform = { search, selectedGroup, storages ->
                val filter = search.searchText
                var filtList = storages
//                if (selectedGroup != null) filtList = filtList.filter { it.group.id == selectedGroup }
                if (filter.isNotBlank()) filtList = filtList.filter { it.filterBy(filter) }
                filtList
            }
        )
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
    }

    override fun deleteGroup(id: Long) = scope.launch {
    }

}