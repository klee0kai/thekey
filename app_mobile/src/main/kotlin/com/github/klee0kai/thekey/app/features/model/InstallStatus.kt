package com.github.klee0kai.thekey.app.features.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
sealed interface InstallStatus : Parcelable

@Parcelize
data object NotInstalled : InstallStatus

@Parcelize
data object Installed : InstallStatus

@Parcelize
data class Installing(
    val progress: Float = 0f,
) : InstallStatus
