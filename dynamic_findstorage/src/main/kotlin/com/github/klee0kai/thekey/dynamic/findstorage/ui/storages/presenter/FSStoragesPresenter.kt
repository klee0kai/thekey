package com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.presenter

import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenter
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface FSStoragesPresenter : StoragesPresenter {

    val externalStoragesColorGroup: Flow<ColorGroup> get() = emptyFlow()

    val isStoragesSearchingProgress: Flow<Boolean> get() = emptyFlow()

    val isPermissionGranted: Flow<Boolean> get() = emptyFlow()

    fun requestPermissions(appRouter: AppRouter?): Job = emptyJob()

    fun searchStorages(): Job = emptyJob()

}