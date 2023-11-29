package com.github.klee0kai.thekey.app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Storage(
    val path: String,
    val name: String?,
    val description: String?
) : Parcelable
