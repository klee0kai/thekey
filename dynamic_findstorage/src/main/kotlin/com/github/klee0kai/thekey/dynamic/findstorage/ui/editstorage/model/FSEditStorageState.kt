package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model

import android.os.Parcelable
import androidx.compose.ui.text.input.TextFieldValue
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.helpers.path.PathInputHelper
import com.github.klee0kai.thekey.core.helpers.path.appendTKeyFormat
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class FSEditStorageState(
    val isEditMode: Boolean = false,
    val isSkeleton: Boolean = false,
    val isSaveAvailable: Boolean = false,
    val isRemoveAvailable: Boolean = false,

    val path: @RawValue TextFieldValue = TextFieldValue(""),
    val name: String = "",
    val desc: String = "",

    val storagePathVariants: List<String> = emptyList(),
    val storagePathFieldExpanded: Boolean = false,

    val colorGroupExpanded: Boolean = false,
    val colorGroupSelectedIndex: Int = 0,
    val colorGroupVariants: List<ColorGroup> = emptyList(),

    ) : Parcelable

fun FSEditStorageState.storage(
    pathInputHelper: PathInputHelper,
    origin: ColoredStorage = ColoredStorage(),
) = with(pathInputHelper) {
    origin.copy(
        path = path.text.absolutePath()?.appendTKeyFormat() ?: "",
        name = name,
        description = desc,
        colorGroup = colorGroupVariants.getOrNull(colorGroupSelectedIndex)
    )
}

fun FSEditStorageState.updateWith(
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