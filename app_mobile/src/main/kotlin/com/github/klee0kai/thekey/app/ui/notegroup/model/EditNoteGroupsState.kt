package com.github.klee0kai.thekey.app.ui.notegroup.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditNoteGroupsState(
    val isEditMode: Boolean = false,
    val isSkeleton: Boolean = false,
    val color: KeyColor = KeyColor.NOCOLOR,
    val name: String = "",
    val selectedNotes: Set<Long> = emptySet(),
) : Parcelable
