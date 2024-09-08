package com.github.klee0kai.thekey.app.ui.hist.presenter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.storage
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.domain.model.updateWith
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class NotePasswHistPresenterImpl(
    val identifier: NoteIdentifier,
) : NoteHistPresenter {

    private val scope = DI.defaultThreadScope()
    private val interactor = DI.notesInteractorLazy(identifier.storage())
    private val clipboardManager by lazy {
        DI.ctx().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }
    private val dateFormat by lazy { TimeFormats.simpleDateFormat() }
    override val searchState = MutableStateFlow(SearchState())

    private val _note = MutableStateFlow<ColoredNote?>(null)
    private val allHistPasswList = flow<List<HistPassw>> {
        _note.map { note -> note?.hist?.map { it.updateWith(dateFormat) } }
            .filterNotNull()
            .collect(this)
    }

    override val filteredHist = flow<List<HistPassw>> {
        combine(
            flow = searchState,
            flow2 = allHistPasswList,
            transform = { search, items ->
                val filter = search.searchText
                var filtList = items
                if (filter.isNotBlank()) filtList = filtList.filter { it.filterBy(filter) }
                filtList
            }
        ).collect(this@flow)
    }.flowOn(DI.defaultDispatcher())

    override fun init() = scope.launch {
        _note.value = interactor().notes.firstOrNull()
            ?.firstOrNull { identifier.notePtr == it.id }
            ?.updateWith(dateFormat)
            ?.copy(isLoaded = false)
        val hist = interactor().note(identifier.notePtr).await().hist
        _note.update { it?.copy(hist = hist) }
    }

    override fun searchFilter(
        newParams: SearchState,
    ) = scope.launch(start = CoroutineStart.UNDISPATCHED) {
        searchState.value = newParams
    }

    override fun copyPassw(
        histPtr: Long,
        router: AppRouter?,
    ) = scope.launch {
        val hist = filteredHist.firstOrNull()
            ?.firstOrNull { it.id == histPtr } ?: return@launch

        val data = ClipData.newPlainText("Password", hist.passw)
        clipboardManager.setPrimaryClip(data)

        router?.snack(R.string.copied_to_clipboard)
    }

    override fun removePassw(
        histPtr: Long,
        router: AppRouter?,
    ) = scope.launch {
        interactor().removeHist(histPtr)
        _note.update { note -> note?.copy(hist = note.hist.filter { it.id != histPtr }) }
    }

}