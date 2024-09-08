package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import kotlinx.parcelize.Parcelize
import java.util.concurrent.TimeUnit

@JniPojo
@Parcelize
data class DecryptedNote(
    val ptnote: Long = 0L,
    val site: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",

    val chTimeSec: Long = 0,
    val colorGroupId: Long = 0,
    val hist: Array<DecryptedPassw> = emptyArray(),
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DecryptedNote

        if (ptnote != other.ptnote) return false
        if (site != other.site) return false
        if (login != other.login) return false
        if (passw != other.passw) return false
        if (desc != other.desc) return false
        if (chTimeSec != other.chTimeSec) return false
        if (colorGroupId != other.colorGroupId) return false
        if (!hist.contentEquals(other.hist)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ptnote.hashCode()
        result = 31 * result + site.hashCode()
        result = 31 * result + login.hashCode()
        result = 31 * result + passw.hashCode()
        result = 31 * result + desc.hashCode()
        result = 31 * result + chTimeSec.hashCode()
        result = 31 * result + colorGroupId.hashCode()
        result = 31 * result + hist.contentHashCode()
        return result
    }

}


fun DecryptedNote.isEmpty(
): Boolean = site.isEmpty() && login.isEmpty() && passw.isEmpty() && desc.isEmpty()

fun DecryptedNote.merge(
    note: DecryptedNote?,
) = if (note != null) {
    DecryptedNote(
        ptnote = if (ptnote != 0L) ptnote else note.ptnote,
        site = site.ifBlank { note.site },
        login = login.ifBlank { note.login },
        passw = passw.ifBlank { note.passw },
        desc = desc.ifBlank { note.desc },
        chTimeSec = if (chTimeSec != 0L) chTimeSec else note.chTimeSec,
        colorGroupId = if (colorGroupId != 0L) colorGroupId else note.colorGroupId,
    )
} else {
    this
}


fun DecryptedNote.coloredNote(
    group: ColorGroup? = null,
    isLoaded: Boolean = false,
    isHistLoaded: Boolean = false,
) = ColoredNote(
    id = ptnote,
    site = site,
    login = login,
    passw = passw,
    desc = desc,
    chTime = TimeUnit.SECONDS.toMillis(chTimeSec),
    group = group ?: ColorGroup(id = colorGroupId),
    hist = hist.map { it.histPasww(isHistLoaded) },
    isLoaded = isLoaded,
)
