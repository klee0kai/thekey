package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
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

    fun searchFilter(newParams: SearchState): Job = emptyJob()

    fun selectGroup(groupId: Long): Job = emptyJob()

    fun setColorGroup(notePt: Long, groupId: Long): Job = emptyJob()

    fun setOtpColorGroup(otpNotePtr: Long, groupId: Long): Job = emptyJob()

    fun deleteGroup(id: Long): Job = emptyJob()

    fun addNewNoteGroup(appRouter: AppRouter?): Job = emptyJob()

}