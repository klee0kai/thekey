package com.github.klee0kai.thekey.core.ui.navigation.model

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int? = null,
    val data: Intent? = null,
    val error: Throwable? = null,
) : Parcelable {

    val isOk get() = resultCode == Activity.RESULT_OK && error == null
    val isCanceled get() = resultCode == Activity.RESULT_CANCELED

}
