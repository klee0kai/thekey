package com.github.klee0kai.thekey.app.ui.genhist.presenter

import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

interface GenHistPresenter {

    val searchState: Flow<SearchState>
        get() = MutableStateFlow(SearchState())

    val filteredHist: Flow<List<HistPassw>> get() = emptyFlow()

    fun searchFilter(newParams: SearchState): Job = emptyJob()

    fun savePassw(histPtr: Long, router: AppRouter?): Job = emptyJob()

    fun copyPassw(histPtr: Long, router: AppRouter?): Job = emptyJob()

    fun removePassw(histPtr: Long, router: AppRouter?): Job = emptyJob()

}