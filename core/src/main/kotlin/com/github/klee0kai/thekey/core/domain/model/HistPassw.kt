package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.util.Date

@Parcelize
data class HistPassw(
    val histPtr: Long = 0,
    val passw: String = "",
    /**
     * change time in milliseconds
     */
    val chTime: Long = 0,

    // meta
    val changeDateStr: String? = null,
    val isLoaded: Boolean = false,
) : Parcelable

fun HistPassw.updateWith(
    dateFormat: DateFormat? = null,
) = copy(
    changeDateStr = dateFormat?.format(Date(chTime)),
)


fun HistPassw.filterBy(filter: String): Boolean {
    return passw.contains(filter, ignoreCase = true)
}