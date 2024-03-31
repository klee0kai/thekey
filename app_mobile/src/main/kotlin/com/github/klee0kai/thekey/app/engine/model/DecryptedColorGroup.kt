package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@JniPojo
@Parcelize
data class DecryptedColorGroup(
    val id: Long = 0L,
    val name: String = "",
    val color: Int,
) : Parcelable

fun DecryptedColorGroup.colorGroup(): KeyColor =
    KeyColor.entries.getOrNull(color) ?: KeyColor.NOCOLOR
