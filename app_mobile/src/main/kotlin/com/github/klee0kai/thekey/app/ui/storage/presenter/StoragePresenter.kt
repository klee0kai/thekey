package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface StoragePresenter {

    val searchState: Flow<SearchState> get() = emptyFlow()

    val selectedGroupId: Flow<Long?> get() = emptyFlow()

    val filteredColorGroups: Flow<List<ColorGroup>> get() = emptyFlow()

    val filteredItems: Flow<List<StorageItem>> get() = emptyFlow()

    fun searchFilter(newParams: SearchState): Job = emptyJob()

    fun selectGroup(groupId: Long): Job = emptyJob()

    fun setColorGroup(notePt: Long, groupId: Long): Job = emptyJob()

    fun setOtpColorGroup(otpNotePtr: Long, groupId: Long): Job = emptyJob()

    fun deleteGroup(id: Long): Job = emptyJob()

    fun addNewNoteGroup(appRouter: AppRouter?): Job = emptyJob()

}