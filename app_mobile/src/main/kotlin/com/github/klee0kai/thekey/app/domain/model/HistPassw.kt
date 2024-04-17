package com.github.klee0kai.thekey.app.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.engine.model.DecryptedPassw
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistPassw(
    val passwPtr: Long = 0,
    val passw: String = "",
    val chTime: Long = 0,

    val isLoaded: Boolean = false,
) : Parcelable

fun DecryptedPassw.histPasww(
    isLoaded: Boolean = false,
) = HistPassw(
    passwPtr = passwPtr,
    passw = passw,
    chTime = chTime,
    isLoaded = isLoaded,
)
