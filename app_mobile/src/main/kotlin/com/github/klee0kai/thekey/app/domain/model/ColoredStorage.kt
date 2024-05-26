package com.github.klee0kai.thekey.app.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredStorage(
    val path: String = "",
    val name: String = "",
    val description: String = "",
    val version: Int = 0,
    val colorGroup: KeyColor? = null
) : Parcelable


fun ColoredStorage.filterBy(filter: String): Boolean {
    return path.contains(filter, ignoreCase = true)
            || name.contains(filter, ignoreCase = true)
}
