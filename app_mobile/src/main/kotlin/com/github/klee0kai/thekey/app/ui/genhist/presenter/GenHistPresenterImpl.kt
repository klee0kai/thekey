package com.github.klee0kai.thekey.app.ui.genhist.presenter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.histPasww
import com.github.klee0kai.thekey.app.ui.navigation.model.EditNoteDestination
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.domain.model.filterBy
import com.github.klee0kai.thekey.core.domain.model.updateWith
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class GenHistPresenterImpl(
    val storageIdentifier: StorageIdentifier,
) : GenHistPresenter {

    private val scope = DI.defaultThreadScope()
    private val engine = DI.cryptStorageEngineSafeLazy(storageIdentifier)
    private val clipboardManager by lazy {
        DI.ctx().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    }
    private val dateFormat by lazy { TimeFormats.simpleDateFormat() }

    override val searchState = MutableStateFlow(SearchState())
    private val allHistPasswList = MutableStateFlow<List<HistPassw>>(emptyList())

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
        reloadHist()
    }

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
            ?.firstOrNull { it.histPtr == histPtr } ?: return@launch

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
            ?.firstOrNull { it.histPtr == histPtr } ?: return@launch

        val data = ClipData.newPlainText("Password", hist.passw)
        clipboardManager.setPrimaryClip(data)

        router?.snack(R.string.copied_to_clipboard)
    }

    override fun removePassw(
        histPtr: Long,
        router: AppRouter?,
    ) = scope.launch {
        val fakeRemove = launch {
            allHistPasswList.update { list -> list.filter { it.histPtr != histPtr } }
        }

        engine().removeHist(histPtr)
        fakeRemove.join()
        reloadHist().join()
    }

    private fun reloadHist() = scope.launch {
        if (allHistPasswList.value.isEmpty()) {
            allHistPasswList.value = engine().genHistory()
                .reversed()
                .map { it.histPasww() }
        }

        allHistPasswList.value = engine().genHistory(info = true)
            .reversed()
            .map { hist ->
                hist.histPasww(isLoaded = true)
                    .updateWith(dateFormat)
            }
    }

}