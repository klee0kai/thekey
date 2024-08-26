package com.github.klee0kai.thekey.app.ui.notegroup.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditNoteGroupsState(
    val isEditMode: Boolean = false,
    val isSkeleton: Boolean = false,
    val isSaveAvailable: Boolean = false,
    val isRemoveAvailable: Boolean = false,
    val colorGroupVariants: List<ColorGroup> = emptyList(),
    val selectedGroupId: Long = 0,
    val name: String = "",
    val selectedNotes: Set<Long> = emptySet(),
) : Parcelable


val EditNoteGroupsState.selectedColorGroup
    get() = colorGroupVariants
        .firstOrNull { selectable -> selectable.id == selectedGroupId }
