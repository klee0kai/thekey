package com.github.klee0kai.thekey.app.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorGroup(
    val id: Long = 0,
    val name: String = "",
    val keyColor: KeyColor = KeyColor.NOCOLOR,

    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}

fun ColorGroup.Companion.noGroup(): ColorGroup =
    ColorGroup(name = DI.ctx().resources.getString(R.string.no), keyColor = KeyColor.NOCOLOR)


