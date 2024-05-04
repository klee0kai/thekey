package com.github.klee0kai.thekey.app.ui.navigation.model

import android.content.res.Resources
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextProvider internal constructor(
    @StringRes private val textRes: Int? = null,
    private val text: String? = null,
) : Parcelable {

    constructor(text: String) : this(textRes = null, text = text)
    constructor(textRes: Int) : this(textRes = textRes, text = null)

    fun text(res: Resources): String {
        return text ?: textRes?.let { res.getString(textRes) } ?: ""
    }

}
