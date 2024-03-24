package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
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
) : Parcelable


fun DecryptedNote.isEmpty(): Boolean =
    site.isEmpty() && login.isEmpty() && passw.isEmpty() && desc.isEmpty()
