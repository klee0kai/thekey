package com.github.klee0kai.thekey.app.ui.changepassw.presenter

import com.github.klee0kai.thekey.app.ui.changepassw.model.ChangePasswordStorageState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

interface ChangeStoragePasswordPresenter {

    val state: Flow<ChangePasswordStorageState> get() = emptyFlow()

    val filteredItems: Flow<List<StorageItem>?>
        get() = MutableStateFlow(null)

    fun init(): Job = emptyJob()

    fun input(block: ChangePasswordStorageState.() -> ChangePasswordStorageState): Job = emptyJob()

    fun save(router: AppRouter?): Job = emptyJob()

}