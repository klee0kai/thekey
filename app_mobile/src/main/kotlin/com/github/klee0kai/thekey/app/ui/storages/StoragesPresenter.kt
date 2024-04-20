package com.github.klee0kai.thekey.app.ui.storages

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.navigation.model.TextProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StoragesPresenter {

    val searchingStoragesStatus = MutableStateFlow(false)

    private val interactor = DI.findStoragesInteractorLazy()
    private val rep = DI.foundStoragesRepositoryLazy()
    private val router = DI.router()
    private val perm = DI.permissionsHelperLazy()
    private val scope = DI.defaultThreadScope()

    private var searchingJob: Job? = null
    private var permissionAsked = false

    fun startup() = scope.launch {
        askPermissionsIfNeed().join()
        findStoragesIfNeed()
    }

    fun coloredGroups() = flow<List<ColorGroup>> {
        rep().getAllColorGroups().await().let { emit(it) }
    }

    fun storages(filter: String = "") = flow<List<ColoredStorage>> {
        interactor().storagesFlow
            .flowOn(Dispatchers.Default)
            .map {
                it.filter { storage ->
                    filter.isBlank() || storage.path.contains(filter)
                }
            }
            .collect(this)
    }


    private fun askPermissionsIfNeed() = scope.launch {
        if (permissionAsked || DI.config().isViewEditMode) return@launch
        permissionAsked = true

        perm().askPermissionsIfNeed(perm().writeStoragePermissions(), TextProvider(R.string.find_external_storage_purpose))
    }

    private fun findStoragesIfNeed() = scope.launch {
        interactor()
            .findStorages()
            .join()
    }.also { job ->
        searchingJob = job
        searchingStoragesStatus.value = job.isCompleted == false
        job.invokeOnCompletion {
            searchingStoragesStatus.value = false
        }
    }
}