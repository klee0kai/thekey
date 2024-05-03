package com.github.klee0kai.thekey.app.ui.settings.plugin.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.PluginIdentifier
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PluginPresenterImpl(
    val identifier: PluginIdentifier = PluginIdentifier(),
) : PluginPresenter {

    private val scope = DI.defaultThreadScope()
    private val manager = DI.dynamicFeaturesManager()

    override val feature = flow {
        manager().features.map { features ->
            features.firstOrNull { it.feature.moduleName == identifier.name }
        }.collect(this)
    }

    override fun install() = scope.launchLatest("install") {
        val feature = feature.first()?.feature ?: return@launchLatest
        manager().install(feature)
    }

    override fun uninstall() = scope.launchLatest("install") {
        val feature = feature.first()?.feature ?: return@launchLatest
        manager().uninstall(feature)
    }

}