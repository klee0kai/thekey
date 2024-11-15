package com.github.klee0kai.thekey.app.ui.otpnote.presenter

import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

interface OtpNotePresenter {

    val otpNote: Flow<ColoredOtpNote> get() = emptyFlow()
    val incrementingTrackFlow: Flow<Int> get() = emptyFlow()

    fun init(): Job = emptyJob()

    fun edit(router: AppRouter?): Job = emptyJob()

    fun copyIssuer(router: AppRouter?): Job = emptyJob()

    fun copyName(router: AppRouter?): Job = emptyJob()

    fun copyCode(router: AppRouter?): Job = emptyJob()

    fun increment(router: AppRouter?): Job = emptyJob()

}

val OtpNotePresenter.isIncrementingFlow get() = incrementingTrackFlow.map { it > 0 }