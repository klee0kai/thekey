package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorGroup(
    /**
     * id > 0 - created color group
     * id [-1 - 0] - no group
     * id [-199 - -100] - predefinded key colors
     * id [-299 - -200] - custom color groups like External or QR
     */
    val id: Long = 0,
    val name: String = "",
    val keyColor: KeyColor = KeyColor.NOCOLOR,
    val isFavorite: Boolean = false,

    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}

fun ColorGroup.Companion.noGroup(): ColorGroup =
    ColorGroup(
        id = 0,
        name = CoreDI.ctx().resources.getString(R.string.no),
        keyColor = KeyColor.NOCOLOR
    )

fun ColorGroup.Companion.externalStorages(): ColorGroup =
    ColorGroup(
        id = -202,
        name = CoreDI.ctx().resources.getString(R.string.ext),
        keyColor = KeyColor.TURQUOISE
    )

fun ColorGroup.Companion.otpNotes(): ColorGroup =
    ColorGroup(
        id = -203,
        name = CoreDI.ctx().resources.getString(R.string.ext),
        keyColor = KeyColor.TURQUOISE
    )
