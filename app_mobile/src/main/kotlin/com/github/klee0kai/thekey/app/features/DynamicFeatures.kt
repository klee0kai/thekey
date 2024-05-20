package com.github.klee0kai.thekey.app.features

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.core.feature.model.DynamicFeature

fun DynamicFeature.Companion.allFeatures() = listOf(
    qrcodeScanner(),
)

fun DynamicFeature.Companion.byName(moduleName: String) = allFeatures().firstOrNull { it.moduleName == moduleName }


fun DynamicFeature.Companion.qrcodeScanner() = DynamicFeature(
    moduleName = "qrcodescanner",
    titleRes = R.string.title_qrcodescanner,
    descRes = R.string.desc_qrcodescanner,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.qrcodescanner.QRCodeScannerImpl",
)

