package com.github.klee0kai.thekey.app.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import kotlinx.parcelize.Parcelize

@Parcelize
@JniPojo
data class Storage(
    val path: String,
    val name: String = "",
    val description: String = ""
) : Parcelable
