package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import kotlinx.parcelize.Parcelize

@JniPojo
@Parcelize
data class GenPasswParams(
    val len: Int = 0,
    val symbolsInPassw: Boolean = false,
    val specSymbolsInPassw: Boolean = false
) : Parcelable
