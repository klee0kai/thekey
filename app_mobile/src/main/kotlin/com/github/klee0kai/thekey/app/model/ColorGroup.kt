package com.github.klee0kai.thekey.app.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorGroup(
    val id: Long = 0,
    val name: String = "",
    val keyColor: KeyColor = KeyColor.TURQUOISE
) : Parcelable {
    companion object;
}

fun ColorGroup.Companion.noGroup(): ColorGroup =
    ColorGroup(name =/* DI.app().resources.getString(R.string.no)*/ "NO", keyColor = KeyColor.NOCOLOR)


