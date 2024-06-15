package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorGroup(
    val id: Long = 0,
    val name: String = "",
    val keyColor: KeyColor = KeyColor.NOCOLOR,
    val isFavorite: Boolean = false,

    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}

fun ColorGroup.Companion.noGroup(): ColorGroup =
    ColorGroup(name = CoreDI.ctx().resources.getString(R.string.no), keyColor = KeyColor.NOCOLOR)

fun ColorGroup.Companion.externalStorages(): ColorGroup =
    ColorGroup(
        id = -2,
        name = CoreDI.ctx().resources.getString(R.string.ext),
        keyColor = KeyColor.TURQUOISE
    )

fun ColorGroup.Companion.otpNotes(): ColorGroup =
    ColorGroup(
        id = -3,
        name = CoreDI.ctx().resources.getString(R.string.ext),
        keyColor = KeyColor.TURQUOISE
    )
