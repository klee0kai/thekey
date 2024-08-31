package com.github.klee0kai.thekey.app.ui.note.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteState(
    val isSkeleton: Boolean = false,
    val siteOrIssuer: String = "",
    val loginOrName: String = "",
    val passw: String = "",
    val desc: String = "",

    val colorGroupExpanded: Boolean = false,
    val colorGroupSelected: Int = 0,
    val colorGroupVariants: List<ColorGroup> = emptyList(),

    val changeTime: String = "",
) : Parcelable