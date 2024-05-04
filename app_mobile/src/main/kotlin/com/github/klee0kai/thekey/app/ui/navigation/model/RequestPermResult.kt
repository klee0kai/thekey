package com.github.klee0kai.thekey.app.ui.navigation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestPermResult(
    val requestCode: Int,
    val permissions: List<String>,
    val grantResults: List<Int>,
) : Parcelable
