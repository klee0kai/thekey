package com.github.klee0kai.thekey.app.ui.settings.plugin.presenter

import com.github.klee0kai.thekey.app.features.model.InstallDynamicFeature
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface PluginPresenter {

    val feature: Flow<InstallDynamicFeature?> get() = emptyFlow()

    fun install(): Job = Job()

    fun uninstall(): Job = Job()

}