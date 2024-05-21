package com.github.klee0kai.thekey.core.perm.model

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class SimplePerm(
    /**
     * Manifest.permission.*
     */
    val perm: String,

    /**
     * User readable text
     */
    @StringRes val desc: Int
) : Parcelable
