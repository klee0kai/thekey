package com.github.klee0kai.thekey.app.di.identifier

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StorageIdentifier(
    val path: String = "",
    val version: Int = 0,
) : Parcelable
