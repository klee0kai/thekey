package com.github.klee0kai.thekey.app.di.identifier

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteIdentifier(
    val storageVersion: Int = 0,
    val storagePath: String = "",
    val notePtr: Long = 0L,
) : Parcelable
