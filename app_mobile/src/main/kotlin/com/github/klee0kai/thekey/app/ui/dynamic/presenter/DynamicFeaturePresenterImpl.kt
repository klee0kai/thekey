package com.github.klee0kai.thekey.app.ui.dynamic.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.features.model.NotInstalled
import com.github.klee0kai.thekey.app.features.model.isCompleted
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class DynamicFeaturePresenterImpl(
    val feature: DynamicFeature,
) : DynamicFeaturePresenter {

    private val scope = DI.defaultThreadScope()
    private val featuresManager = DI.dynamicFeaturesManager()
    private val router = DI.router()

    override val status = flow {
        featuresManager()
            .features
            .map { features ->
                features.firstOrNull { it.feature.moduleName == feature.moduleName }
                    ?.status ?: NotInstalled
            }
            .collect(this)
    }

    override fun install() = scope.launchLatest("install") {
        featuresManager().install(feature)
        status.firstOrNull { it.isCompleted }

        router.showInitDynamicFeatureScreen.value = true
        delay(1000)

        delay(1000)
        router.showInitDynamicFeatureScreen.value = false
    }

}