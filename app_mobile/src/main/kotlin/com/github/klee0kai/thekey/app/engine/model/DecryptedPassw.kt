package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import kotlinx.parcelize.Parcelize

@JniPojo
@Parcelize
data class DecryptedPassw(
    val passwPtr: Long = 0,
    val passw: String = "",
    val chTime: Long = 0,
) : Parcelable



fun DecryptedPassw.histPasww(
    isLoaded: Boolean = false,
) = HistPassw(
    passwPtr = passwPtr,
    passw = passw,
    chTime = chTime,
    isLoaded = isLoaded,
)