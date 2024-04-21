package com.github.klee0kai.thekey.app.ui.settings.plugin

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.PluginIdentifier
import com.github.klee0kai.thekey.app.features.model.InstallDynamicFeature
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PluginPresenter(
    val identifier: PluginIdentifier = PluginIdentifier(),
) {

    private val scope = DI.defaultThreadScope()
    private val manager = DI.dynamicFeaturesManager()

    val feature = flow<InstallDynamicFeature?> {
        manager().features.map { features ->
            features.firstOrNull { it.feature.moduleName == identifier.name }
        }.collect(this)
    }

    fun install() = scope.launchLatest("install") {
        val feature = feature.first()?.feature ?: return@launchLatest
        manager().install(feature)
    }

    fun uninstall() = scope.launchLatest("install") {
        val feature = feature.first()?.feature ?: return@launchLatest
        manager().uninstall(feature)
    }

}