package com.github.klee0kai.thekey.core.domain.model

import com.github.klee0kai.thekey.core.domain.model.feature.PaidFeature

enum class HistPeriod {
    /**
     * 1 month
     */
    SHORT,

    /**
     * 3 month
     */
    NORMAL,

    /**
     * 6 month.
     * Available only for pro
     *
     * @see PaidFeature.UNLIMITED_HIST_PERIOD
     */
    LONG,

    /**
     * 12 month.
     * Available only for pro
     *
     * @see PaidFeature.UNLIMITED_HIST_PERIOD
     */
    VERY_LONG,
}

fun HistPeriod.nextRecursive(): HistPeriod = when (this) {
    HistPeriod.SHORT -> HistPeriod.NORMAL
    HistPeriod.NORMAL -> HistPeriod.LONG
    HistPeriod.LONG -> HistPeriod.VERY_LONG
    HistPeriod.VERY_LONG -> HistPeriod.SHORT
}