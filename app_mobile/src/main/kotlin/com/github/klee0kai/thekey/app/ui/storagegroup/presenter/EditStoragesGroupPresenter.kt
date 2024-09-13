package com.github.klee0kai.thekey.app.ui.storagegroup.presenter

import com.github.klee0kai.thekey.app.ui.storagegroup.model.EditStorageGroupsState
import com.github.klee0kai.thekey.app.ui.storagegroup.model.SelectedStorage
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface EditStoragesGroupPresenter {

    val variants: Flow<List<ColorGroup>> get() = emptyFlow()

    val state: Flow<EditStorageGroupsState> get() = emptyFlow()

    val allStorages: Flow<List<SelectedStorage>> get() = emptyFlow()

    fun input(block: EditStorageGroupsState.() -> EditStorageGroupsState): Job = emptyJob()

    fun init(): Job = emptyJob()

    fun save(router: AppRouter?): Job = emptyJob()

    fun remove(router: AppRouter?): Job = emptyJob()

}


fun EditStoragesGroupPresenter.selectStorage(path: String, selected: Boolean) = input {
    copy(
        selectedStorages = if (!selected) {
            selectedStorages.toMutableSet().apply { remove(path) }
        } else {
            selectedStorages + setOf(path)
        },
    )
}

