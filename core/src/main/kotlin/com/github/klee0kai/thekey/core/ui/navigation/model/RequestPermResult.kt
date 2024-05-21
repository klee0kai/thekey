package com.github.klee0kai.thekey.core.ui.navigation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestPermResult(
    val requestCode: Int,
    val permissions: List<String>,
    val grantResults: List<Int>,
) : Parcelable
