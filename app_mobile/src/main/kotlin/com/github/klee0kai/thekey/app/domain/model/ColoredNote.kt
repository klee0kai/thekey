package com.github.klee0kai.thekey.app.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.core.domain.ColorGroup
import com.github.klee0kai.thekey.core.domain.noGroup
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredNote(
    val ptnote: Long = 0L,
    val site: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",

    val group: ColorGroup = ColorGroup.noGroup(),
    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}

fun DecryptedNote.coloredNote(
    group: ColorGroup? = null,
    isLoaded: Boolean = false,
) = ColoredNote(
    ptnote = ptnote,
    site = site,
    login = login,
    passw = passw,
    desc = desc,
    group = group ?: ColorGroup(id = colorGroupId),
    isLoaded = isLoaded,
)
