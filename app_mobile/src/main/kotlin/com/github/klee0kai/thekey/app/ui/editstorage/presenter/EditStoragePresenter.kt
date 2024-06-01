package com.github.klee0kai.thekey.app.ui.editstorage.presenter

import com.github.klee0kai.thekey.app.ui.editstorage.model.EditStorageState
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface EditStoragePresenter {

    val state: Flow<EditStorageState> get() = emptyFlow()

    fun init(): Job = emptyJob()

    fun input(block: EditStorageState.() -> EditStorageState): Job = emptyJob()

    fun remove(): Job = emptyJob()

    fun save(): Job = emptyJob()

}