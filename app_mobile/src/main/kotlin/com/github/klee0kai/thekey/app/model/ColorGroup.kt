package com.github.klee0kai.thekey.app.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorGroup(
    val name: String = "",
    val colorGroup: KeyColor = KeyColor.TURQUOISE
) : Parcelable
