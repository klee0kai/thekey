package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.thedeanda.lorem.LoremIpsum
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoredOtpNote(
    val ptnote: Long = 0L,
    val issuer: String = "",
    val name: String = "",

    val group: ColorGroup = ColorGroup.noGroup(),
    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}

@DebugOnly
fun ColoredOtpNote.Companion.dummyLoaded() =
    ColoredOtpNote(
        ptnote = Dummy.dummyId,
        issuer = LoremIpsum.getInstance().url,
        name = LoremIpsum.getInstance().name,
        group = ColorGroup.dummy(),
        isLoaded = true,
    )


@DebugOnly
fun ColoredOtpNote.Companion.dummySkeleton() =
    ColoredOtpNote(
        ptnote = Dummy.dummyId,
        isLoaded = false,
    )
