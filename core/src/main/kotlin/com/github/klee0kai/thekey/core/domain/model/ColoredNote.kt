package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredNote(
    val ptnote: Long = 0L,
    val site: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",

    val group: ColorGroup = ColorGroup.noGroup(),
    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}
