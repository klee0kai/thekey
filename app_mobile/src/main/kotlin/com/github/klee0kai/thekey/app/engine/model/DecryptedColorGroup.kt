package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import com.github.klee0kai.thekey.core.domain.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import kotlinx.parcelize.Parcelize

@JniPojo
@Parcelize
data class DecryptedColorGroup(
    val id: Long = 0L,
    val name: String = "",
    val color: Int = 0,
) : Parcelable

fun DecryptedColorGroup.keyColor(): KeyColor =
    KeyColor.entries.getOrNull(color) ?: KeyColor.NOCOLOR

fun DecryptedColorGroup.colorGroup(
    isLoaded: Boolean = false,
) = ColorGroup(
    id = id,
    name = name,
    keyColor = keyColor(),
    isLoaded = isLoaded,
)

