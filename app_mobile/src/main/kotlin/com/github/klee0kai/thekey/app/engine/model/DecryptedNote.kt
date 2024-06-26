package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import kotlinx.parcelize.Parcelize

@JniPojo
@Parcelize
data class DecryptedNote(
    val ptnote: Long = 0L,
    val site: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",

    val chTime: Long = 0,
    val colorGroupId: Long = 0,
) : Parcelable


fun DecryptedNote.isEmpty(): Boolean =
    site.isEmpty() && login.isEmpty() && passw.isEmpty() && desc.isEmpty()

fun DecryptedNote.merge(note: DecryptedNote?) =
    if (note != null) {
        DecryptedNote(
            ptnote = if (ptnote != 0L) ptnote else note.ptnote,
            site = site.ifBlank { note.site },
            login = login.ifBlank { note.login },
            passw = passw.ifBlank { note.passw },
            desc = desc.ifBlank { note.desc },
            chTime = if (chTime != 0L) chTime else note.chTime,
            colorGroupId = if (colorGroupId != 0L) colorGroupId else note.colorGroupId,
        )
    } else {
        this
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
