package com.github.klee0kai.thekey.app.ui.settings.plugin.presenter

import com.github.klee0kai.thekey.core.feature.model.InstallStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface PluginPresenter {

    val status: Flow<InstallStatus> get() = emptyFlow()

    fun install(): Job = Job()

    fun uninstall(): Job = Job()

}