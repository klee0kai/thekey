package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredOtpNote(
    val ptnote: Long = 0L,
    val issuer: String = "",
    val name: String = "",

    val group: ColorGroup = ColorGroup.noGroup(),
    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}
