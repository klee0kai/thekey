package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model

import android.os.Parcelable
import androidx.compose.ui.text.input.TextFieldValue
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class FSEditStorageState(
    val isEditMode: Boolean = false,
    val isSkeleton: Boolean = false,
    val isSaveAvailable: Boolean = false,
    val isRemoveAvailable: Boolean = false,

    val pathNoExt: @RawValue TextFieldValue = TextFieldValue(""),
    val name: String = "",
    val desc: String = "",

    val storagePathVariants: List<String> = emptyList(),
    val storagePathFieldFocused: Boolean = false,

    val colorGroupExpanded: Boolean = false,
    val colorGroupSelected: Int = 0,
    val colorGroupVariants: List<ColorGroup> = emptyList(),

    ) : Parcelable

fun FSEditStorageState.storage(origin: ColoredStorage = ColoredStorage()) =
    origin.copy(
        name = name,
        description = desc,
        colorGroup = colorGroupVariants.getOrNull(colorGroupSelected)
    )

fun FSEditStorageState.updateWith(
    storage: ColoredStorage? = null,
    colorGroups: List<ColorGroup> = emptyList(),
) = copy(
    name = storage?.name ?: "",
    desc = storage?.description ?: "",
    colorGroupVariants = colorGroups,
    colorGroupSelected = colorGroups
        .indexOfFirst { storage?.colorGroup?.id == it.id }
        .takeIf { it >= 0 } ?: 0,
)