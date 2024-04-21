package com.github.klee0kai.thekey.app.ui.settings.plugins

import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.flow.flow

class PluginsPresenter {

    private val scope = DI.defaultThreadScope()
    private val manager = DI.dynamicFeaturesManager()

    val features = flow {
        manager().features.collect(this)
    }

}