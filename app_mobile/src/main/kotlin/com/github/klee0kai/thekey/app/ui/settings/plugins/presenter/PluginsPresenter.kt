package com.github.klee0kai.thekey.app.ui.settings.plugins.presenter

import com.github.klee0kai.thekey.app.features.model.InstallDynamicFeature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface PluginsPresenter {

    val features: Flow<List<InstallDynamicFeature>> get() = emptyFlow()

}