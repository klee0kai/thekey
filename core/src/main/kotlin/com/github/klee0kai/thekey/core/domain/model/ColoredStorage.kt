package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredStorage(
    val path: String = "",
    val name: String = "",
    val description: String = "",
    val version: Int = 0,
    val colorGroup: ColorGroup? = null
) : Parcelable


fun ColoredStorage.filterBy(filter: String): Boolean {
    return path.contains(filter, ignoreCase = true)
            || name.contains(filter, ignoreCase = true)
}

fun ColoredStorage.sortableFlatText(): String {
    return "${path}-${name}-${description}"
}

fun ColoredStorage.isValid() = path.isNotBlank() && version > 0