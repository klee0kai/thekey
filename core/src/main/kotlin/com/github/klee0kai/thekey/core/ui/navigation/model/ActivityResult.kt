package com.github.klee0kai.thekey.core.ui.navigation.model

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivityResult(
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?,
) : Parcelable {

    val isOk get() = resultCode == Activity.RESULT_OK
    val isCanceled get() = resultCode == Activity.RESULT_CANCELED

}
