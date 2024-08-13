package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.thedeanda.lorem.LoremIpsum
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredNote(
    val ptnote: Long = 0L,
    val site: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",

    val group: ColorGroup = ColorGroup.noGroup(),
    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}

@DebugOnly
fun ColoredNote.Companion.dummyLoaded() =
    ColoredNote(
        ptnote = Dummy.dummyId,
        site = LoremIpsum.getInstance().url,
        login = LoremIpsum.getInstance().name,
        passw = LoremIpsum.getInstance().getWords(1),
        desc = LoremIpsum.getInstance().getWords(5),
        group = ColorGroup.dummy(),
        isLoaded = true,
    )

@DebugOnly
fun ColoredNote.Companion.dummySkeleton() =
    ColoredNote(
        ptnote = Dummy.dummyId,
        isLoaded = false,
    )
