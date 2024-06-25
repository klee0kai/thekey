package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistPassw(
    val passwPtr: Long = 0,
    val passw: String = "",
    val chTime: Long = 0,

    val isLoaded: Boolean = false,
) : Parcelable

