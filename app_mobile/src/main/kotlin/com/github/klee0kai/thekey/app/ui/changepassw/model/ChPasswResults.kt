package com.github.klee0kai.thekey.app.ui.changepassw.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface ChPasswResults : Parcelable

@Parcelize
data object ChPasswConfirmed : ChPasswResults

@Parcelize
data object ChPasswCanceled : ChPasswResults
