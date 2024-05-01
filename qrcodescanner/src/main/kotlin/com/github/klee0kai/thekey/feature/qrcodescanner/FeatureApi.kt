package com.github.klee0kai.thekey.feature.qrcodescanner

import com.github.klee0kai.thekey.app.di.AppComponent
import com.github.klee0kai.thekey.app.features.model.FeatureLibApi
import com.github.klee0kai.thekey.feature.qrcodescanner.di.AndroidHelpersModuleQRExt

@Suppress("unused")
class FeatureApiImpl : FeatureLibApi {

    override fun AppComponent.initDI() {
        initAndroidHelpersModule(
            AndroidHelpersModuleQRExt(androidHelpersFactory())
        )
    }

}