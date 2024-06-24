package com.github.klee0kai.thekey.app.ui.settings.plugin.presenter

import com.github.klee0kai.thekey.core.domain.model.feature.model.InstallStatus
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface PluginPresenter {

    val status: Flow<InstallStatus> get() = emptyFlow()

    fun install(router: AppRouter?): Job = emptyJob()

    fun uninstall(router: AppRouter?): Job = emptyJob()

}