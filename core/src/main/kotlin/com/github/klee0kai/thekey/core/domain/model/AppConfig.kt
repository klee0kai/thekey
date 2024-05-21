package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppConfig(
    val isDebug: Boolean = false,
    val isViewEditMode: Boolean = false,

    ) : Parcelable
