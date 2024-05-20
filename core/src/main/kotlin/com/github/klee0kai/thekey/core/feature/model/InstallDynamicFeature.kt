package com.github.klee0kai.thekey.core.feature.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InstallDynamicFeature(
    val feature: DynamicFeature = DynamicFeature(),

    val status: InstallStatus = NotInstalled,
) : Parcelable
