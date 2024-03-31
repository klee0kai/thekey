package com.github.klee0kai.thekey.app.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredStorage(
    val path: String = "",
    val name: String = "",
    val description: String = "",
    val version: Int = 0,
    val colorGroup: KeyColor? = null

) : Parcelable
