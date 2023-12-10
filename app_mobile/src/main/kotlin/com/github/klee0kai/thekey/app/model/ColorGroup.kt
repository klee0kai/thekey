package com.github.klee0kai.thekey.app.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.ui.designkit.color.ColoredStorageGroup
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorGroup(
    val name: String = "",
    val colorGroup: ColoredStorageGroup = ColoredStorageGroup.TURQUOISE
) : Parcelable
