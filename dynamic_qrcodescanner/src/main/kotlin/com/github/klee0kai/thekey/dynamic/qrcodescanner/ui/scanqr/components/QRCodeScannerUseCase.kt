package com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr.components

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCase
import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.github.klee0kai.thekey.app.di.DI
import com.google.android.play.core.splitinstall.SplitInstallHelper
import com.google.mlkit.common.internal.CommonComponentRegistrar
import com.google.mlkit.common.sdkinternal.MlKitContext
import com.google.mlkit.dynamic.DynamicLoadingRegistrar
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.internal.BarcodeRegistrar
import com.google.mlkit.vision.common.internal.VisionCommonRegistrar
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

private val inited = AtomicBoolean(false)

fun Context.qrCodeUserScanner(
    onFound: (List<Barcode>) -> Unit = {}
): UseCase? = runCatching {
    if (!inited.getAndSet(true)) {
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
    }


    val barcodeScanner = BarcodeScanning
        .getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

    val mlKitAnalyzer: ImageAnalysis.Analyzer = MlKitAnalyzer(
        listOf(barcodeScanner),
        ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
        DI.defaultExecutor(),
    ) { result: MlKitAnalyzer.Result? ->
        val qrCodes = result?.getValue(barcodeScanner)
        if (!qrCodes.isNullOrEmpty()) {
            onFound.invoke(qrCodes)
        }
    }

    val qrCodeAnalyser: ImageAnalysis = ImageAnalysis.Builder()
        .build()
        .apply {
            setAnalyzer(DI.defaultExecutor(), mlKitAnalyzer)
        }

    qrCodeAnalyser
}.onFailure {
    Timber.e(it)
}.getOrNull()