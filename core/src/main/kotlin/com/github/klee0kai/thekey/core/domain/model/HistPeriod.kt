package com.github.klee0kai.thekey.core.domain.model

import com.github.klee0kai.thekey.core.domain.model.feature.PaidFeature
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HistPeriod {
    /**
     * 1 month
     */
    @SerialName("short")
    SHORT,

    /**
     * 3 month
     */
    @SerialName("normal")
    NORMAL,

    /**
     * 6 month.
     * Available only for pro
     *
     * @see PaidFeature.UNLIMITED_HIST_PERIOD
     */
    @SerialName("long")
    LONG,

    /**
     * 12 month.
     * Available only for pro
     *
     * @see PaidFeature.UNLIMITED_HIST_PERIOD
     */
    @SerialName("very_long")
    VERY_LONG,
}

fun HistPeriod.nextRecursive(): HistPeriod = when (this) {
    HistPeriod.SHORT -> HistPeriod.NORMAL
    HistPeriod.NORMAL -> HistPeriod.LONG
    HistPeriod.LONG -> HistPeriod.VERY_LONG
    HistPeriod.VERY_LONG -> HistPeriod.SHORT
}