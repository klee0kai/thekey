package com.github.klee0kai.thekey.dynamic.findstorage.ui.settings.presenter

import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface FSSettingsPresenter {

    val isAutoSearchEnable: Flow<Boolean> get() = emptyFlow()

    fun init():Job = emptyJob()

    fun toggleAutoSearch(appRouter: AppRouter?): Job = emptyJob()

}