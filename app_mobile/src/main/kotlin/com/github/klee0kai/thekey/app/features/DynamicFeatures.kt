package com.github.klee0kai.thekey.app.features

import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature

fun DynamicFeature.Companion.allFeatures() = listOf(
    findStorage(),
    qrcodeScanner(),
    autofill(),
    gdrive(),
)

fun DynamicFeature.Companion.byName(moduleName: String) = allFeatures().firstOrNull { it.moduleName == moduleName }

fun DynamicFeature.Companion.qrcodeScanner() = DynamicFeature(
    moduleName = "qrcodescanner",
    titleRes = R.string.title_qrcodescanner,
    descRes = R.string.desc_qrcodescanner,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.qrcodescanner.QRCodeScannerImpl",
    isCommunity = true,
)

fun DynamicFeature.Companion.findStorage() = DynamicFeature(
    moduleName = "dynamic_findstorage",
    titleRes = R.string.title_findstorages,
    descRes = R.string.desc_findstorages,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.findstorage.FindStorageImpl",
    isCommunity = true,
)

fun DynamicFeature.Companion.autofill() = DynamicFeature(
    moduleName = "dynamic_autofill",
    titleRes = R.string.title_autofill,
    descRes = R.string.desc_autofill,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.autofill.AutofillImpl",
)


fun DynamicFeature.Companion.gdrive() = DynamicFeature(
    moduleName = "dynamic_gdrive",
    titleRes = R.string.title_gdrive,
    descRes = R.string.desc_gdrvie,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.gdrive.GDriveImpl",
)

