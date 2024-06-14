package com.github.klee0kai.thekey.app.ui.storages.presenter

import android.content.Intent
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.filterBy
import com.github.klee0kai.thekey.app.domain.model.sortableFlatText
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.domain.ColorGroup
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.file.writeAndClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileInputStream


open class StoragesPresenterImpl : StoragesPresenter {

    private val ctx = DI.ctx()
    private val rep = DI.storagesRepositoryLazy()
    private val interactor = DI.storagesInteractorLazy()
    private val scope = DI.defaultThreadScope()

    override val searchState = MutableStateFlow(SearchState())
    override val selectedGroupId = MutableStateFlow<Long?>(null)

    private val sortedStorages = flow {
        interactor().allStorages
            .map { list -> list.sortedBy { storage -> storage.sortableFlatText() } }
            .collect(this)
    }

    override val filteredColorGroups = flow {
        rep().allColorGroups.collect(this)
    }.flowOn(DI.defaultDispatcher())

    override val filteredStorages = flow {
        combine(
            flow = searchState,
            flow2 = selectedGroupId,
            flow3 = sortedStorages,
            transform = { search, selectedGroup, storages ->
                val filter = search.searchText
                var filtList = storages
                if (selectedGroup != null) filtList = filtList.filter { it.colorGroup?.id == selectedGroup }
                if (filter.isNotBlank()) filtList = filtList.filter { it.filterBy(filter) }
                filtList
            }
        ).collect(this)
    }.flowOn(DI.defaultDispatcher())

    override fun searchFilter(newParams: SearchState) = scope.launch {
        searchState.value = newParams
    }

    override fun selectGroup(groupId: Long) = scope.launch {
        if (selectedGroupId.value == groupId) {
            selectedGroupId.value = null
        } else {
            selectedGroupId.value = groupId
        }
    }


    override fun setColorGroup(storagePath: String, groupId: Long) = scope.launch {
        val storage = rep().findStorage(storagePath).await() ?: return@launch
        rep().setStorage(storage.copy(colorGroup = ColorGroup(id = groupId)))
    }

    override fun deleteGroup(id: Long) = scope.launch {
        rep().deleteColorGroup(id)
    }

    override fun exportStorage(storagePath: String, router: AppRouter) = scope.launch {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            .apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                setType("application/ckey")
                putExtra(Intent.EXTRA_TITLE, File(storagePath).name)
            }
        val createDocResult = router.navigate(intent)
            .firstOrNull()
        if (createDocResult?.error != null) Timber.e(createDocResult.error, "error create file to save")

        val url = createDocResult?.data?.data ?: return@launch

        try {
            FileInputStream(storagePath)
                .writeAndClose(ctx.contentResolver.openOutputStream(url))
        } catch (e: Throwable) {
            Timber.e(createDocResult.error, "error to export storage")
        }

    }

    override fun editStorage(storagePath: String, router: AppRouter) = scope.launch {
        router.navigate(EditStorageDestination(path = storagePath))
            .firstOrNull()
    }

}