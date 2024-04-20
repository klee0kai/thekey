package com.github.klee0kai.thekey.app.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.BuildConfig
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppConfig(
    val isDebug: Boolean = BuildConfig.DEBUG,
    val isViewEditMode: Boolean = false,

    ) : Parcelable
