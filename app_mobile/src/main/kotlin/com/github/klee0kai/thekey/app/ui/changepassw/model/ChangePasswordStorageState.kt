package com.github.klee0kai.thekey.app.ui.changepassw.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChangePasswordStorageState(
    val currentPassw: String = "",
    val newPassw: String = "",
    val newPasswConfirm: String = "",
    val isSaveAvailable: Boolean = false,


) : Parcelable