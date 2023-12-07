package com.github.klee0kai.thekey.app.ui.storages

import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class StoragesPresenter {

    val searchingStoragesStatus = MutableStateFlow(false)

    private val interactor by DI.findStoragesInteractorLazy()
    private val navigator = DI.navigator()
    private val scope = DI.mainThreadScope()

    private var searchingJob: Job? = null

    private val mockedStoragesFlow = flowOf(
        (0..100).map {
            Storage("/path${it}", "name${it}", "description${it}")
        }
    )

    init {
        searchingJob = interactor.findStorages().also { job ->
            searchingStoragesStatus.value = job.isCompleted == false
            job.invokeOnCompletion {
                searchingStoragesStatus.value = false
            }
        }
    }

    fun storages(filter: String = "") =
        mockedStoragesFlow
            .flowOn(Dispatchers.Default)
            .map {
                it.filter { storage ->
                    filter.isBlank() || storage.path.contains(filter)
                }
            }


}