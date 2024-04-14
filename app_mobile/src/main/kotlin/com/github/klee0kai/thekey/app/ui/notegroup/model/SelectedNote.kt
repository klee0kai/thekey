package com.github.klee0kai.thekey.app.ui.notegroup.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.domain.model.ColoredNote
import com.github.klee0kai.thekey.app.domain.model.noGroup
import kotlinx.parcelize.Parcelize


@Parcelize
data class SelectedNote(
    val ptnote: Long = 0L,
    val site: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",

    val group: ColorGroup = ColorGroup.noGroup(),
    val selected: Boolean = false,
) : Parcelable {
    companion object;
}

fun ColoredNote.selected(selected: Boolean = false) = SelectedNote(
    ptnote = ptnote,
    site = site,
    login = login,
    passw = passw,
    desc = desc,
    group = group,
    selected = selected,
)
