package com.github.klee0kai.thekey.core.domain.model

import android.os.Parcelable
import com.github.klee0kai.thekey.core.domain.basemodel.BaseModel
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.util.Date

@Parcelize
data class HistPassw(
    override val id: Long = 0,
    val passw: String = "",
    /**
     * change time in milliseconds
     */
    val chTime: Long = 0,

    // meta
    val changeDateStr: String? = null,
    override val isLoaded: Boolean = false,
) : Parcelable, BaseModel<Long> {
    companion object;

    override fun filterBy(filter: String): Boolean {
        return passw.contains(filter, ignoreCase = true)
    }

    override fun sortableFlatText(): String = chTime.toString()

}

fun HistPassw.updateWith(
    dateFormat: DateFormat? = null,
) = copy(
    changeDateStr = dateFormat?.format(Date(chTime)),
)

