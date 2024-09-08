package com.github.klee0kai.thekey.app.ui.hist.presenter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteDestination
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.domain.model.updateWith
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class GenPasswHistPresenterImpl(
    val storageIdentifier: StorageIdentifier,
) : GenHistPresenter {

    private val scope = DI.defaultThreadScope()
    private val interactor = DI.genPasswInteractorLazy(storageIdentifier)
    private val clipboardManager by lazy {
        DI.ctx().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }
    private val dateFormat by lazy { TimeFormats.simpleDateFormat() }

    override val searchState = MutableStateFlow(SearchState())
    private val allHistPasswList = flow<List<HistPassw>> {
        interactor().history
            .map { list -> list.map { it.updateWith(dateFormat) } }
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

    override fun searchFilter(
        newParams: SearchState,
    ) = scope.launch(start = CoroutineStart.UNDISPATCHED) {
        searchState.value = newParams
    }

    override fun savePassw(
        histPtr: Long,
        router: AppRouter?,
    ) = scope.launch {
        val hist = filteredHist.firstOrNull()
            ?.firstOrNull { it.id == histPtr } ?: return@launch

        router?.navigate(
            EditNoteDestination(
                path = storageIdentifier.path,
                storageVersion = storageIdentifier.version,
                note = DecryptedNote(passw = hist.passw)
            )
        )
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
    }

}