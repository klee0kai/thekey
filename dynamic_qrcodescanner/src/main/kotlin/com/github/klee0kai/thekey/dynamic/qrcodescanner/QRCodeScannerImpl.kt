package com.github.klee0kai.thekey.dynamic.qrcodescanner

import com.github.klee0kai.thekey.app.di.AppComponent
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.features.model.FeatureLibApi
import com.github.klee0kai.thekey.dynamic.qrcodescanner.di.AndroidHelpersModuleQRExt
import com.google.android.play.core.splitinstall.SplitInstallHelper
import com.google.mlkit.common.MlKit
import com.google.mlkit.common.internal.CommonComponentRegistrar
import com.google.mlkit.common.sdkinternal.MlKitContext
import com.google.mlkit.dynamic.DynamicLoadingRegistrar
import com.google.mlkit.vision.barcode.internal.BarcodeRegistrar
import com.google.mlkit.vision.common.internal.VisionCommonRegistrar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@Suppress("unused")
class QRCodeScannerImpl : FeatureLibApi {

    override fun AppComponent.initDI() {
        GlobalScope.launch {
            delay(1.seconds)
            kotlin.runCatching {

                SplitInstallHelper.loadLibrary(DI.ctx(), "barhopper_v3")
                SplitInstallHelper.loadLibrary(DI.ctx(), "image_processing_util_jni")

                MlKitContext.initialize(
                    DI.ctx(),
                    listOf(
                        VisionCommonRegistrar(),
                        CommonComponentRegistrar(),
                        DynamicLoadingRegistrar(),
                        BarcodeRegistrar()
                    )
                )

            }.onFailure { Timber.e(it) }
        }

        initCoreAndroidHelpersModule(
            AndroidHelpersModuleQRExt(coreAndroidHelpersModuleFactory())
        )
    }


}