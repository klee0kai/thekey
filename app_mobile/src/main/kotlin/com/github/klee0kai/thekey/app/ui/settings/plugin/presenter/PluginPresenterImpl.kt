package com.github.klee0kai.thekey.app.ui.settings.plugin.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.configRouting
import com.github.klee0kai.thekey.app.di.updateComponentsSoft
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.domain.model.feature.model.NotInstalled
import com.github.klee0kai.thekey.core.domain.model.feature.model.isCompleted
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.launchLatest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

open class PluginPresenterImpl(
    val feature: DynamicFeature,
) : PluginPresenter {

    private val scope = DI.defaultThreadScope()
    private val featuresManager = DI.dynamicFeaturesManager()

    override val status = flow {
        featuresManager()
            .features
            .map { features ->
                features.firstOrNull { it.feature.moduleName == feature.moduleName }
                    ?.status ?: NotInstalled
            }
            .collect(this)
    }

    override fun install(router: AppRouter) = scope.launchLatest("install") {
        featuresManager().install(feature)
        status.firstOrNull { it.isCompleted }

        router.softUpdateFeatures()
    }

    override fun uninstall(router: AppRouter) = scope.launchLatest("install") {
        featuresManager().uninstall(feature)

        router.softUpdateFeatures()
    }

    protected open suspend fun AppRouter.softUpdateFeatures() {
        showInitDynamicFeatureScreen.value = true
        delay(1000)
        DI.updateComponentsSoft()
        DI.configRouting()
        delay(100)
        showInitDynamicFeatureScreen.value = false
    }

}