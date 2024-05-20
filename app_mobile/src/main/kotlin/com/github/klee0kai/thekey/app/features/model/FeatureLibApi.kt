package com.github.klee0kai.thekey.app.features.model

import com.github.klee0kai.thekey.app.di.AppComponent
import com.github.klee0kai.thekey.core.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.utils.common.JvmReflection

interface FeatureLibApi {

    fun AppComponent.initDI() = Unit

}


fun DynamicFeature.findApi(): FeatureLibApi? = with(JvmReflection) {
    featureLibApiClass?.let {
        createNew<FeatureLibApi>(it)
    }
}