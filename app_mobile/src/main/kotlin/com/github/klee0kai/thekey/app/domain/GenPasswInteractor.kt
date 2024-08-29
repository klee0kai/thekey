package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.GenPasswParams
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.domain.model.HistPeriod
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.common.months
import com.github.klee0kai.thekey.core.utils.common.years
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration.Companion.days

class GenPasswInteractor(
    val identifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val billing = DI.billingInteractor()
    private val settings = DI.settingsRepositoryLazy()
    private val rep = DI.genHistoryRepositoryLazy(identifier)

    val history = flow<List<HistPassw>> {
        rep().allHistPasswList.collect(this)
    }.flowOn(DI.defaultDispatcher())

    suspend fun lastGeneratedPassw() = scope.async {
        rep().lastGeneratedPassw()
    }

    fun generateNewPassw(params: GenPasswParams) = scope.async {
        rep().generateNewPassw(params).also {
            cleanOldHistIfNeed()
        }
    }

    fun removeHist(histPtr: Long) = scope.launch {
        rep().removeHist(histPtr)
    }

    fun cleanOldHistIfNeed() = scope.launch(globalRunDesc = R.string.clean_old_hist) {
        val now = System.currentTimeMillis()
        if (now - settings().lastCleanHistTime() < CLEAN_PERIOD.inWholeMilliseconds) {
            // already cleaned
            return@launch
        }
        settings().lastCleanHistTime.set(now)
        val period = when (settings().histPeriod()) {
            HistPeriod.SHORT -> 1.months
            HistPeriod.NORMAL -> 3.months
            HistPeriod.LONG -> 6.months
            HistPeriod.VERY_LONG -> 1.years
        }
        rep().cleanOld((now - period.inWholeMilliseconds))
    }

    companion object {
        val CLEAN_PERIOD = 1.days
    }

}