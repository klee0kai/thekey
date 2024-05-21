package com.github.klee0kai.thekey.core.di.identifiers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StorageIdentifier(
    val path: String = "",
    val version: Int = 0,
) : Parcelable
