package com.github.klee0kai.thekey.app.ui.selectstorageboard.presenter

import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface SelectStorageToNoteMoveBoardPresenter {

    val currentStorage: Flow<ColoredStorage?> get() = emptyFlow()

    val openedStoragesFlow: Flow<List<ColoredStorage>> get() = emptyFlow()

    fun select(storagePath: String, router: AppRouter?): Job = emptyJob()

}