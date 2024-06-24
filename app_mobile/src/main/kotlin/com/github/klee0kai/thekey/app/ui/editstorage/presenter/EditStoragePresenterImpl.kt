package com.github.klee0kai.thekey.app.ui.editstorage.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.editstorage.model.EditStorageState
import com.github.klee0kai.thekey.app.ui.editstorage.model.storage
import com.github.klee0kai.thekey.app.ui.editstorage.model.updateWith
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.noGroup
import com.github.klee0kai.thekey.core.helpers.path.appendTKeyFormat
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.error.FSDuplicateError
import com.github.klee0kai.thekey.core.utils.error.FSNoAccessError
import com.github.klee0kai.thekey.core.utils.error.FSNoFileName
import com.github.klee0kai.thekey.core.utils.error.cause
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class EditStoragePresenterImpl(
    val storageIdentifier: StorageIdentifier?,
) : EditStoragePresenter {

    private val scope = DI.defaultThreadScope()
    private val storagesInteractor = DI.storagesInteractorLazy()
    private val interactor = DI.editStorageInteractorLazy()
    private val settingsRep = DI.settingsRepositoryLazy()
    private val userShortPaths = DI.userShortPaths()
    private val appFolder by lazy { DI.userShortPaths().appPath }

    private var originStorage: ColoredStorage? = null
    private var colorGroups: List<ColorGroup> = emptyList()

    override val state = MutableStateFlow(EditStorageState(isSkeleton = true))

    override fun init() = scope.launch {
        if (!state.value.isSkeleton) return@launch

        val colorGroupUpdate = launch {
            colorGroups = storagesInteractor().allColorGroups
                .first()
                .let { listOf(ColorGroup.noGroup()) + it }
        }

        originStorage = storageIdentifier?.path?.let {
            storagesInteractor().findStorage(it).await()
        }

        val initState = EditStorageState(
            isEditMode = originStorage != null,
            isRemoveAvailable = originStorage != null,
            name = originStorage?.name ?: "",
            desc = originStorage?.description ?: "",
        )
        state.update { initState }
        colorGroupUpdate.join()
        state.update {
            it.updateWith(
                storage = originStorage,
                colorGroups = colorGroups
            ).copy(
                isEditMode = false,
            )
        }
    }

    override fun input(block: EditStorageState.() -> EditStorageState) = scope.launch(DI.mainDispatcher()) {
        var newState = block.invoke(state.value)
        val fulfilled = newState.name.isNotBlank()
        val isSaveAvailable = when {
            originStorage != null -> fulfilled && newState.storage(originStorage!!) != originStorage
            else -> fulfilled
        }
        newState = newState.copy(
            isSaveAvailable = isSaveAvailable,
            isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
        )
        state.value = newState
    }


    override fun remove(router: AppRouter?) = scope.launch {
        val path = originStorage?.path ?: return@launch
        interactor().deleteStorage(path).await()
        router?.snack(R.string.storage_deleted)
        backFromScreen(router)
    }

    override fun save(router: AppRouter?) = scope.launch {
        val curState = state.value
        var storage = curState.storage(originStorage ?: ColoredStorage(version = settingsRep().newStorageVersion()))
        if (storage.path.isBlank() || userShortPaths.isAppInnerExternal(storage.path)) {
            storage = storage.copy(
                path = File(appFolder, curState.name)
                    .absolutePath
                    .appendTKeyFormat()
            )
        }

        val (result, messageRes) = when {
            originStorage == null -> {
                val result = interactor().createStorage(storage).await()
                result to R.string.storage_created
            }

            originStorage?.path != storage.path -> {
                val result = interactor().moveStorage(originStorage!!.path, storage).await()
                result to R.string.storage_moved
            }

            else -> {
                val result = interactor().setStorage(storage).await()
                result to R.string.storage_saved
            }
        }

        val error = result.exceptionOrNull()
        when {
            error == null && result.isSuccess -> {
                router?.snack(messageRes)
                backFromScreen(router)
            }

            error?.cause(FSNoFileName::class) != null -> router?.snack(R.string.fill_the_file_name)
            error?.cause(FSDuplicateError::class) != null -> router?.snack(R.string.duplicate)
            error?.cause(FSNoAccessError::class) != null -> router?.snack(R.string.no_access)
            else -> router?.snack(R.string.unknown_error)
        }
    }

    private fun backFromScreen(router: AppRouter?) {
        clean()
        router?.back()
    }

    private fun clean() = input { copy(isSkeleton = true) }

}