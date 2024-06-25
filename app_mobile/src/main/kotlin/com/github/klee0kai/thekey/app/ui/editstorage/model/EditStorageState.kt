package com.github.klee0kai.thekey.app.ui.editstorage.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditStorageState(
    val isEditMode: Boolean = false,
    val isSkeleton: Boolean = false,
    val isSaveAvailable: Boolean = false,
    val isRemoveAvailable: Boolean = false,

    val name: String = "",
    val desc: String = "",

    val colorGroupExpanded: Boolean = false,
    val colorGroupSelectedIndex: Int = 0,
    val colorGroupVariants: List<ColorGroup> = emptyList(),

    ) : Parcelable

fun EditStorageState.storage(origin: ColoredStorage = ColoredStorage()) =
    origin.copy(
        name = name,
        description = desc,
        colorGroup = colorGroupVariants.getOrNull(colorGroupSelectedIndex)
    )

fun EditStorageState.updateWith(
    storage: ColoredStorage? = null,
    colorGroups: List<ColorGroup> = emptyList(),
) = copy(
    name = storage?.name ?: "",
    desc = storage?.description ?: "",
    colorGroupVariants = colorGroups,
    colorGroupSelectedIndex = colorGroups
        .indexOfFirst { storage?.colorGroup?.id == it.id }
        .takeIf { it >= 0 } ?: 0,
)