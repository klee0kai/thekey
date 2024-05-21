package com.github.klee0kai.thekey.dynamic.qrcodescanner

import com.github.klee0kai.thekey.app.di.AppComponent
import com.github.klee0kai.thekey.app.features.model.FeatureLibApi
import com.github.klee0kai.thekey.dynamic.qrcodescanner.di.AndroidHelpersModuleQRExt

@Suppress("unused")
class QRCodeScannerImpl : FeatureLibApi {

    override fun AppComponent.initDI() {
        initCoreAndroidHelpersModule(
            AndroidHelpersModuleQRExt(coreAndroidHelpersModuleFactory())
        )
    }

}