package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.presenter

import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.FSEditStorageState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface FSEditStoragePresenter {

    val state: Flow<FSEditStorageState> get() = emptyFlow()

    fun init(): Job = emptyJob()

    fun input(block: FSEditStorageState.() -> FSEditStorageState): Job = emptyJob()

    fun remove(router: AppRouter?): Job = emptyJob()

    fun save(router: AppRouter?): Job = emptyJob()

}