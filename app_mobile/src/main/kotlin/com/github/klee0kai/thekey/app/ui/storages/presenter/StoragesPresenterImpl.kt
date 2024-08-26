package com.github.klee0kai.thekey.app.ui.storages.presenter

import com.github.klee0kai.hummus.collections.contains
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.data.mapping.toColoredStorage
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.features.findStorage
import com.github.klee0kai.thekey.app.ui.navigation.createFileIntent
import com.github.klee0kai.thekey.app.ui.navigation.model.BackupStorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageGroupDestination
import com.github.klee0kai.thekey.app.ui.navigation.openFileIntent
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.externalStorages
import com.github.klee0kai.thekey.core.domain.model.feature.PaidFeature
import com.github.klee0kai.thekey.core.domain.model.feature.PaidLimits
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.domain.model.filterBy
import com.github.klee0kai.thekey.core.domain.model.sortableFlatText
import com.github.klee0kai.thekey.core.helpers.path.tKeyExtension
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.file.createNewWithSuffix
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import com.github.klee0kai.thekey.core.R as CoreR


open class StoragesPresenterImpl : StoragesPresenter {

    private val ctx = DI.ctx()
    private val installFindStoragePresenter = DI.pluginPresenter(DynamicFeature.findStorage())
    private val rep = DI.storagesRepositoryLazy()
    private val interactor = DI.storagesInteractorLazy()
    private val settings = DI.settingsRepositoryLazy()
    private val scope = DI.defaultThreadScope()
    private val shortPath = DI.userShortPaths()
    private val engine = DI.findStorageEngineSaveLazy()
    private val dateFormat by lazy { SimpleDateFormat.getDateInstance() }
    private val billing = DI.billingInteractor()

    override val installAutoSearchStatus = installFindStoragePresenter.status

    override val searchState = MutableStateFlow(SearchState())
    override val selectedGroupId = MutableStateFlow<Long?>(null)

    override val selectableColorGroups = flow {
        rep().allColorGroups.collect(this)
    }.flowOn(DI.defaultDispatcher())

    override val filteredColorGroups = flow {
        val extGroup = interactor().externalStoragesGroup.first()
        selectableColorGroups
            .map { list ->
                buildList {
                    if (settings().externalStoragesGroup() && !list.contains { it.id == extGroup.id })
                        add(extGroup)
                    addAll(list)
                }
            }
            .collect(this)
    }.flowOn(DI.defaultDispatcher())

    private val sortedStorages = flow {
        interactor().allStorages
            .map { list -> list.sortedBy { storage -> storage.sortableFlatText() } }
            .collect(this)
    }

    override val filteredStorages = flow {
        combine(
            flow = searchState,
            flow2 = selectedGroupId,
            flow3 = sortedStorages,
            transform = { search, selectedGroup, storages ->
                val filterExt = selectedGroup == ColorGroup.externalStorages().id
                val filter = search.searchText
                var filtList = storages
                if (filterExt) filtList = filtList.filter { shortPath.isExternal(it.path) }
                else if (selectedGroup != null) filtList =
                    filtList.filter { it.colorGroup?.id == selectedGroup }
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

    override fun exportStorage(storagePath: String, router: AppRouter?) = scope.launch {
        val createDocResult = router?.navigate(createFileIntent(File(storagePath).name))
            ?.firstOrNull()
        if (createDocResult?.error != null) Timber.e(
            createDocResult.error,
            "error create file to save"
        )

        val url = createDocResult?.data?.data ?: return@launch
        try {
            FileInputStream(storagePath).use { input ->
                ctx.contentResolver.openOutputStream(url)?.use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Throwable) {
            Timber.e(createDocResult.error, "error to export storage")
        }
    }

    override fun addNewStorageGroup(appRouter: AppRouter?) = scope.launch {
        val currentColorGroups = filteredColorGroups.firstOrNull()?.size ?: 0
        if (billing.isAvailable(PaidFeature.UNLIMITED_STORAGE_GROUPS)
            || currentColorGroups < PaidLimits.PAID_STORAGE_GROUPS_LIMIT
        ) {
            appRouter?.navigate(EditStorageGroupDestination())
        } else {
            appRouter?.snack(R.string.limited_in_free_version)
        }
    }

    override fun editStorage(storagePath: String, router: AppRouter?) = scope.launch {
        router?.navigate(EditStorageDestination(path = storagePath))
            ?.firstOrNull()
    }

    override fun backupStorage(storagePath: String, router: AppRouter?) = scope.launch {
        router?.navigate(BackupStorageDestination(StorageIdentifier(path = storagePath)))
            ?.firstOrNull()
    }

    override fun importStorage(appRouter: AppRouter?) = scope.launch {
        val openResult = appRouter?.navigate(openFileIntent())
            ?.firstOrNull()
        if (openResult?.error != null) Timber.e(openResult.error, "import storage err")
        val url = openResult?.data?.data ?: return@launch

        val filename = dateFormat.format(Date())
        val newStorageFile = File("${shortPath.appPath}/${filename}.$tKeyExtension")
            .createNewWithSuffix()
        newStorageFile.parentFile?.mkdirs()

        try {
            ctx.contentResolver.openInputStream(url)?.use { input ->
                FileOutputStream(newStorageFile).use { output ->
                    input.copyTo(output)
                }
            }
            val storageInfo = engine().storageInfo(path = newStorageFile.absolutePath)
            if (storageInfo == null) {
                appRouter.snack(CoreR.string.storage_file_incorrect)
                newStorageFile.delete()
            } else {
                rep().setStorage(storageInfo.toColoredStorage())
                selectedGroupId.value = null
                appRouter.snack(CoreR.string.storage_imported)
            }
        } catch (e: Throwable) {
            Timber.e(openResult.error ?: e, "error to import storage")
        }
    }

    override fun installAutoSearchPlugin(appRouter: AppRouter?) =
        installFindStoragePresenter.install(appRouter)

}