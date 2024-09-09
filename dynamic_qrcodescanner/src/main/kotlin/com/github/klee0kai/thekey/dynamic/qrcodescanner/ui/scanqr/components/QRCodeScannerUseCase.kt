package com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr.components

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCase
import androidx.camera.mlkit.vision.MlKitAnalyzer
import com.github.klee0kai.thekey.app.di.DI
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import timber.log.Timber

fun Context.qrCodeUserScanner(
    onFound: (List<Barcode>) -> Unit = {}
): UseCase? = runCatching {
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