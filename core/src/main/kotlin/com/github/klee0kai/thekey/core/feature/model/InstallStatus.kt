package com.github.klee0kai.thekey.core.feature.model

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
data object InstallError : InstallStatus

@Parcelize
data class Installing(
    val progress: Float = 0f,
) : InstallStatus

val InstallStatus.isInstalled get() = this is Installed

val InstallStatus.isCompleted get() = this is Installed || this is InstallError

val InstallStatus.isNotInstalled get() = this is NotInstalled || this is InstallError
