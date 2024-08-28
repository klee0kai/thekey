package com.github.klee0kai.thekey.app.ui.genhist.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.histPasww
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.filterBy
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.domain.model.filterBy
import com.github.klee0kai.thekey.core.domain.model.updateWith
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import com.github.klee0kai.thekey.core.utils.coroutine.touchable
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

open class GenHistPresenterImpl(
    val storageIdentifier: StorageIdentifier,
) : GenHistPresenter {

    private val scope = DI.defaultThreadScope()
    private val engine = DI.cryptStorageEngineSafeLazy(storageIdentifier)
    private val dateFormat by lazy { TimeFormats.simpleDateFormat() }

    override val searchState = MutableStateFlow(SearchState())

    private val allHistPasswList = flow {
        engine().genHistory()
            .reversed()
            .map { it.histPasww() }
            .also { emit(it) }

        engine().genHistory(info = true)
            .reversed()
            .map { hist ->
                hist.histPasww(isLoaded = true)
                    .updateWith(dateFormat)
            }
            .also { emit(it) }
    }.flowOn(DI.defaultDispatcher())
        .touchable()

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

}