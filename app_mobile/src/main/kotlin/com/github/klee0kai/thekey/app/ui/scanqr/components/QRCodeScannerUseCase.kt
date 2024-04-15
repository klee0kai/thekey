package com.github.klee0kai.thekey.app.ui.scanqr.components

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.UseCase
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

fun Context.qrCodeUserScanner(
    onFound: (List<Barcode>) -> Unit = {}
): UseCase? = runCatching {
    val context = this
    val barcodeScanner = BarcodeScanning
        .getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

    val mlKitAnalyzer: ImageAnalysis.Analyzer = MlKitAnalyzer(
        listOf(barcodeScanner),
        ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
        ContextCompat.getMainExecutor(context),
    ) { result: MlKitAnalyzer.Result? ->
        val qrCodes = result?.getValue(barcodeScanner)
        if (!qrCodes.isNullOrEmpty()) {
            onFound.invoke(qrCodes)
        }
    }

    val qrCodeAnalyser: ImageAnalysis = ImageAnalysis.Builder()
        .build()
        .apply {
            setAnalyzer(ContextCompat.getMainExecutor(context), mlKitAnalyzer)
        }

    qrCodeAnalyser
}.getOrNull()