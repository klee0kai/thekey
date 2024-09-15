package com.github.klee0kai.thekey.app.ui.simpleboard.presenter

import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface SimpleBoardPresenter {

    val currentStorage: Flow<ColoredStorage?> get() = emptyFlow()

    val openedStoragesFlow: Flow<List<ColoredStorage>> get() = emptyFlow()

    val favoritesStorages: Flow<List<ColoredStorage>> get() = emptyFlow()

    fun openStorage(storagePath: String, router: AppRouter?): Job = emptyJob()

    fun logout(storagePath: String, router: AppRouter?): Job = emptyJob()

}