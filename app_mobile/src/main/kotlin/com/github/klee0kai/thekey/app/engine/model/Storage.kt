package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import kotlinx.parcelize.Parcelize

@Parcelize
@JniPojo
data class Storage(
    val path: String = "",

    /**
     * sha256 of salt
     */
    val salt: String = "",
    val name: String = "",
    val description: String = "",

    val version: Int = 0,

    // opened storage info
    val logined: Boolean = false,
) : Parcelable
