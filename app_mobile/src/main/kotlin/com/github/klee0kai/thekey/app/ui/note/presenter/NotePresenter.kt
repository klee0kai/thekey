package com.github.klee0kai.thekey.app.ui.note.presenter

import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface NotePresenter {

    val state: Flow<ColoredNote> get() = emptyFlow()

    fun init(): Job = emptyJob()

    fun showHistory(router: AppRouter?): Job = emptyJob()

    fun edit(router: AppRouter?): Job = emptyJob()

    fun copySite(router: AppRouter?): Job = emptyJob()

    fun copyLogin(router: AppRouter?): Job = emptyJob()

    fun copyPassw(router: AppRouter?): Job = emptyJob()

    fun copyDesc(router: AppRouter?): Job = emptyJob()

}