package com.github.klee0kai.thekey.app.features

import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature

fun DynamicFeature.Companion.allFeatures() = listOf(
    commercial(),
    findStorage(),
    qrcodeScanner(),
    autofill(),
    gdrive(),
    chpassw(),
    smpassw(),
)

fun DynamicFeature.Companion.visiblePlugins() = allFeatures().filter { !it.isHidden }

fun DynamicFeature.Companion.byName(moduleName: String) =
    allFeatures().firstOrNull { it.moduleName == moduleName }

fun DynamicFeature.Companion.commercial() = DynamicFeature(
    moduleName = "dynamic_commercial",
    titleRes = R.string.title_commercial,
    descRes = R.string.desc_commercial,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.commercial.CommercialImpl",
    isCommunity = true,
    isHidden = true,
)

fun DynamicFeature.Companion.qrcodeScanner() = DynamicFeature(
    moduleName = "dynamic_qrcodescanner",
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

fun DynamicFeature.Companion.chpassw() = DynamicFeature(
    moduleName = "dynamic_gdrive",
    titleRes = R.string.title_ch_passw,
    descRes = R.string.desc_ch_passw,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.chpassw.ChPasswImpl",
)

fun DynamicFeature.Companion.smpassw() = DynamicFeature(
    moduleName = "dynamic_smpassw",
    titleRes = R.string.title_smpassw,
    descRes = R.string.desc_smpassw,
    featureLibApiClass = "com.github.klee0kai.thekey.dynamic.similar.SmPasswImpl",
)



