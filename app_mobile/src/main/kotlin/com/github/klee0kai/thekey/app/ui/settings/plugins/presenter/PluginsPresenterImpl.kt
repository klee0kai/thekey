package com.github.klee0kai.thekey.app.ui.settings.plugins.presenter

import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

open class PluginsPresenterImpl : PluginsPresenter {

    private val scope = DI.defaultThreadScope()
    private val manager = DI.dynamicFeaturesManagerLazy()

    override val features = flow {
        manager().features
            .map { list -> list.filter { !it.feature.isHidden } }
            .collect(this)
    }

}