package com.github.klee0kai.thekey.app.ui.genhist.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.domain.model.HistPassw
import com.github.klee0kai.thekey.core.domain.model.updateWith
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.common.TimeFormats
import com.thedeanda.lorem.LoremIpsum
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

open class GenHistPresenterDummy(
    private val histCount: Int = 30,
) : GenHistPresenter {

    private val scope = DI.defaultThreadScope()

    override val searchState = MutableStateFlow(SearchState())
    private val dateFormat by lazy { TimeFormats.simpleDateFormat() }


    override val filteredHist = MutableStateFlow(
        buildList {
            repeat(histCount) {
                add(
                    HistPassw(
                        Dummy.dummyId,
                        passw = LoremIpsum().getWords(1),
                        isLoaded = Random.nextInt(3) != 1,
                        chTime = System.currentTimeMillis(),
                    ).updateWith(dateFormat),
                )
            }
        }
    )

    override fun searchFilter(
        newParams: SearchState,
    ) = scope.launch(start = CoroutineStart.UNDISPATCHED) {
        searchState.value = newParams
    }

}