package com.github.klee0kai.thekey.app.ui.storage.genpassw.model

import android.os.Parcelable
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenPasswState(
    val passwLen: Int = 0,
    val symInPassw: Boolean = false,
    val specSymbolsInPassw: Boolean = false,
    val passw: String = "",
) : Parcelable


fun GenPasswState.toGenParams() = GenPasswParams(
    len = passwLen,
    symbolsInPassw = symInPassw,
    specSymbolsInPassw = specSymbolsInPassw,
)