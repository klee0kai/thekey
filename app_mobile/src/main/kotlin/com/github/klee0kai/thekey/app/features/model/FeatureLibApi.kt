package com.github.klee0kai.thekey.app.features.model

import com.github.klee0kai.thekey.app.di.AppComponent
import com.github.klee0kai.thekey.core.ui.navigation.deeplink.DeeplinkHandler
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.utils.common.JvmReflection

interface FeatureLibApi {

    fun DeeplinkHandler.configDeeplinks() = Unit

    fun AppComponent.initDI() = Unit

}


fun DynamicFeature.findApi(): FeatureLibApi? = with(JvmReflection) {
    featureLibApiClass?.let {
        createNew<FeatureLibApi>(it)
    }
}