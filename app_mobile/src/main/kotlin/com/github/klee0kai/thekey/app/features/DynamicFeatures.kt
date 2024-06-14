package com.github.klee0kai.thekey.app.features

import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature

fun DynamicFeature.Companion.allFeatures() = listOf(
    qrcodeScanner(),
    autofill(),
)

fun DynamicFeature.Companion.byName(moduleName: String) = allFeatures().firstOrNull { it.moduleName == moduleName }


fun DynamicFeature.Companion.qrcodeScanner() = DynamicFeature(
    moduleName = "qrcodescanner",
    titleRes = R.string.title_qrcodescanner,
    descRes = R.string.desc_qrcodescanner,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.qrcodescanner.QRCodeScannerImpl",
    isCommunity = true,
)

fun DynamicFeature.Companion.autofill() = DynamicFeature(
    moduleName = "dynamic_autofill",
    titleRes = R.string.title_autofill,
    descRes = R.string.desc_autofill,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.autofill.AutofillImpl",
)

