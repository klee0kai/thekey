package com.github.klee0kai.thekey.app.ui.storagegroup.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.externalStorages
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditStorageGroupsState(
    val isEditMode: Boolean = false,
    val isSkeleton: Boolean = false,
    val isSaveAvailable: Boolean = false,
    val isRemoveAvailable: Boolean = false,
    val isCanSelectStorages: Boolean = false,
    val colorGroupVariants: List<ColorGroup> = emptyList(),
    val selectedGroupId: Long = 0,
    val name: String = "",
    val selectedStorages: Set<String> = emptySet(),
    val isFavorite: Boolean = false,
) : Parcelable


fun EditStorageGroupsState.colorGroup(origin: ColorGroup = ColorGroup()): ColorGroup {
    val ext = ColorGroup.externalStorages()
    if (selectedGroupId == ext.id) {
        return origin.copy(
            id = selectedGroupId,
            name = name.takeIf { it.isNotBlank() } ?: ext.name,
            keyColor = colorGroupVariants.firstOrNull { it.id == selectedGroupId }?.keyColor ?: KeyColor.NOCOLOR,
            isFavorite = isFavorite,
        )
    }

    return origin.copy(
        name = name,
        keyColor = colorGroupVariants.firstOrNull { it.id == selectedGroupId }?.keyColor ?: KeyColor.NOCOLOR,
        isFavorite = isFavorite,
    )
}