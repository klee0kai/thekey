package com.github.klee0kai.thekey.app.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredOtpNote(
    val ptnote: Long = 0L,
    val issuer: String = "",
    val name: String = "",

    val group: ColorGroup = ColorGroup.noGroup(),
    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}

fun DecryptedOtpNote.coloredNote(
    group: ColorGroup? = null,
    isLoaded: Boolean = false,
) = ColoredOtpNote(
    ptnote = ptnote,

    group = group ?: ColorGroup(id = colorGroupId),
    isLoaded = isLoaded,
)
