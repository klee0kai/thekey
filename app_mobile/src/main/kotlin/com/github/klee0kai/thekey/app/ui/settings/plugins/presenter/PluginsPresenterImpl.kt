package com.github.klee0kai.thekey.app.ui.settings.plugins.presenter

import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.flow.flow

open class PluginsPresenterImpl : PluginsPresenter {

    private val scope = DI.defaultThreadScope()
    private val manager = DI.dynamicFeaturesManager()

    override val features = flow {
        manager().features.collect(this)
    }

}