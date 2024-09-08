package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.domain.basemodel.BaseModel
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorGroup(
    /**
     * id > 0 - created color group
     * id [-1 - 0] - no group
     * id [-199 - -100] - predefinded key colors
     * id [-299 - -200] - custom color groups like External or QR
     */
    override val id: Long = 0,
    val name: String = "",
    val keyColor: KeyColor = KeyColor.NOCOLOR,
    val isFavorite: Boolean = false,

    override val isLoaded: Boolean = false,
) : Parcelable, BaseModel<Long> {
    companion object;

    override fun filterBy(filter: String): Boolean {
        return name.contains(filter, ignoreCase = true)
    }

    override fun sortableFlatText(): String = name

}

fun ColorGroup.Companion.noGroup(): ColorGroup =
    ColorGroup(
        id = 0,
        name = CoreDI.ctx().resources.getString(R.string.no),
        keyColor = KeyColor.NOCOLOR
    )

fun ColorGroup.Companion.externalStorages(): ColorGroup =
    ColorGroup(
        id = -202,
        name = CoreDI.ctx().resources.getString(R.string.ext),
        keyColor = KeyColor.TURQUOISE
    )

fun ColorGroup.Companion.otpNotes(): ColorGroup =
    ColorGroup(
        id = -203,
        name = CoreDI.ctx().resources.getString(R.string.ext),
        keyColor = KeyColor.TURQUOISE
    )

@DebugOnly
fun ColorGroup.Companion.dummy() =
    ColorGroup(
        id = Dummy.dummyId,
        name = "qwerty".random().toString(),
        keyColor = KeyColor.entries.random(),
    )
