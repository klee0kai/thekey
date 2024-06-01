package com.github.klee0kai.thekey.app.ui.storagegroup.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditStorageGroupsState(
    val isEditMode: Boolean = false,
    val isSkeleton: Boolean = false,
    val isSaveAvailable: Boolean = false,
    val isRemoveAvailable: Boolean = false,
    val color: KeyColor = KeyColor.NOCOLOR,
    val name: String = "",
    val selectedStorages: Set<String> = emptySet(),
    val isFavorite: Boolean = false,
) : Parcelable


fun EditStorageGroupsState.colorGroup(origin: ColorGroup = ColorGroup()) = origin.copy(
    name = name,
    keyColor = color,
    isFavorite = isFavorite,
)