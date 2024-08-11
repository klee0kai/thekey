package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import kotlinx.parcelize.Parcelize

@JniPojo
@Parcelize
data class ChPasswStrategy(
    val currentPasswd: String = "",
    val newPassw: String = "",
    val defaultStrategy: Boolean = false,
    val noteIds: LongArray = LongArray(0),
    val otpNoteIds: LongArray = LongArray(0),
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChPasswStrategy

        if (currentPasswd != other.currentPasswd) return false
        if (newPassw != other.newPassw) return false
        if (defaultStrategy != other.defaultStrategy) return false
        if (!noteIds.contentEquals(other.noteIds)) return false
        if (!otpNoteIds.contentEquals(other.otpNoteIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = currentPasswd.hashCode()
        result = 31 * result + newPassw.hashCode()
        result = 31 * result + defaultStrategy.hashCode()
        result = 31 * result + noteIds.contentHashCode()
        result = 31 * result + otpNoteIds.contentHashCode()
        return result
    }


}

