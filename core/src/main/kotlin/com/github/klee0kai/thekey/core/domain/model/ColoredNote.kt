package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import com.thedeanda.lorem.LoremIpsum
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.util.Date

@Parcelize
data class ColoredNote(
    val ptnote: Long = 0L,
    val site: String = "",
    val login: String = "",
    val passw: String = "",
    val desc: String = "",
    val chTime: Long = 0L,

    // combined
    val group: ColorGroup = ColorGroup.noGroup(),
    val hist: List<HistPassw> = emptyList(),

    // meta
    val changeDateStr: String? = null,
    val isLoaded: Boolean = false,
) : Parcelable {
    companion object;
}

fun ColoredNote.updateWith(
    dateFormat: DateFormat? = null,
) = copy(
    changeDateStr = dateFormat?.format(Date(chTime)),
)

@DebugOnly
fun ColoredNote.Companion.dummyLoaded(

) = ColoredNote(
    ptnote = Dummy.dummyId,
    site = LoremIpsum.getInstance().url,
    login = LoremIpsum.getInstance().name,
    passw = LoremIpsum.getInstance().getWords(1),
    desc = LoremIpsum.getInstance().getWords(5),
    chTime = System.currentTimeMillis(),
    group = ColorGroup.dummy(),
    isLoaded = true,
).updateWith(TimeFormats.simpleDateFormat())

@DebugOnly
fun ColoredNote.Companion.dummySkeleton() =
    ColoredNote(
        ptnote = Dummy.dummyId,
        isLoaded = false,
    )
