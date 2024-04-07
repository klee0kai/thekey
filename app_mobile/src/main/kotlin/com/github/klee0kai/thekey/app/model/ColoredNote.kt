package com.github.klee0kai.thekey.app.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredNote(
    val ptnote: Long = 0L,
    val site: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",

    val group: ColorGroup = ColorGroup.noGroup()
) : Parcelable {
    companion object;
}


fun DecryptedNote.coloredNote(
    group: ColorGroup = ColorGroup.noGroup()
) = ColoredNote(
    ptnote = ptnote,
    site = site,
    login = login,
    passw = passw,
    desc = desc,
    group = group,
)
