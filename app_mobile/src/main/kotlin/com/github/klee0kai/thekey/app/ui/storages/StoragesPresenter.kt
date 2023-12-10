package com.github.klee0kai.thekey.app.ui.storages

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.ColoredStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StoragesPresenter {

    val searchingStoragesStatus = MutableStateFlow(false)

    private val interactor = DI.findStoragesInteractorLazy()
    private val navigator = DI.navigator()
    private val scope = DI.mainThreadScope()

    private var searchingJob: Job? = null


    init {
        searchingJob = scope.launch {
            interactor()
                .findStorages()
                .join()
        }.also { job ->
            searchingStoragesStatus.value = job.isCompleted == false
            job.invokeOnCompletion {
                searchingStoragesStatus.value = false
            }
        }
    }

    suspend fun storages(filter: String = ""): Flow<List<ColoredStorage>> {
        return interactor().storagesFlow
            .flowOn(Dispatchers.Default)
            .map {
                it.filter { storage ->
                    filter.isBlank() || storage.path.contains(filter)
                }
            }
    }


}