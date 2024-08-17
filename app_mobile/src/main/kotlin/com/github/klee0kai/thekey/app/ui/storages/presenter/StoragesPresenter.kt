package com.github.klee0kai.thekey.app.ui.storages.presenter

import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.feature.model.InstallStatus
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface StoragesPresenter {

    val installAutoSearchStatus: Flow<InstallStatus> get() = emptyFlow()

    val searchState: Flow<SearchState> get() = emptyFlow()

    val selectedGroupId: Flow<Long?> get() = emptyFlow()

    val selectableColorGroups: Flow<List<ColorGroup>> get() = emptyFlow()

    val filteredColorGroups: Flow<List<ColorGroup>> get() = emptyFlow()

    val filteredStorages: Flow<List<ColoredStorage>> get() = emptyFlow()

    fun init(): Job = emptyJob()

    fun searchFilter(newParams: SearchState): Job = emptyJob()

    fun selectGroup(groupId: Long): Job = emptyJob()

    fun setColorGroup(storagePath: String, groupId: Long): Job = emptyJob()

    fun deleteGroup(id: Long): Job = emptyJob()

    fun addNewStorage(appRouter: AppRouter?): Job = emptyJob()

    fun editStorage(storagePath: String, router: AppRouter?): Job = emptyJob()

    fun backupStorage(storagePath: String, router: AppRouter?): Job = emptyJob()

    fun exportStorage(storagePath: String, router: AppRouter?): Job = emptyJob()

    fun importStorage(appRouter: AppRouter?): Job = emptyJob()

    fun installAutoSearchPlugin(appRouter: AppRouter?): Job = emptyJob()

}