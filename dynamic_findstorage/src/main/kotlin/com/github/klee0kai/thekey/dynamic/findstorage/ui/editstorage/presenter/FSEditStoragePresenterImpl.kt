package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.presenter

import androidx.compose.ui.text.input.TextFieldValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.noGroup
import com.github.klee0kai.thekey.core.helpers.path.removeTKeyFormat
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.launchLatest
import com.github.klee0kai.thekey.core.utils.error.FSDuplicateError
import com.github.klee0kai.thekey.core.utils.error.FSNoAccessError
import com.github.klee0kai.thekey.core.utils.error.FSNoFileName
import com.github.klee0kai.thekey.core.utils.error.cause
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.FSEditStorageState
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.storage
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.updateWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FSEditStoragePresenterImpl(
    val storageIdentifier: StorageIdentifier?,
) : FSEditStoragePresenter {

    private val scope = FSDI.defaultThreadScope()
    private val storagesInteractor = FSDI.storagesInteractorLazy()
    private val interactor = FSDI.editStorageInteractorLazy()
    private val settingsRep = FSDI.settingsRepositoryLazy()
    private val pathInputHelper = FSDI.pathInputHelper()

    private var originStorage: ColoredStorage? = null
    private var colorGroups: List<ColorGroup> = emptyList()

    override val state = MutableStateFlow(FSEditStorageState(isSkeleton = true))

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

        with(pathInputHelper) {
            val initState = FSEditStorageState(
                isEditMode = originStorage != null,
                isRemoveAvailable = originStorage != null,
                path = TextFieldValue(originStorage?.path?.shortPath()?.removeTKeyFormat() ?: ""),
                name = originStorage?.name ?: "",
                desc = originStorage?.description ?: "",
            )
            state.update { initState }
        }

        colorGroupUpdate.join()
        state.update {
            it.updateWith(
                storage = originStorage,
                colorGroups = colorGroups
            )
        }
    }

    override fun input(block: FSEditStorageState.() -> FSEditStorageState) = scope.launch(DI.mainDispatcher()) {
        state.update {
            var newState = block(it)
            val fulfilled = newState.path.text.isNotBlank()
                    && !newState.path.text.endsWith("/")
                    && newState.name.isNotBlank()
            val isSaveAvailable = when {
                originStorage != null -> fulfilled && newState.storage(pathInputHelper, originStorage!!) != originStorage
                else -> fulfilled
            }
            newState = newState.copy(
                isSaveAvailable = isSaveAvailable,
                isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
            )

            newState
        }

        updatePathVariants()
    }

    override fun remove(router: AppRouter?) = scope.launch {
        val path = originStorage?.path ?: return@launch
        interactor().deleteStorage(path).await()
        router?.snack(R.string.storage_deleted)
        backFromScreen(router)
    }

    override fun save(router: AppRouter?) = scope.launch {
        val curState = state.value
        val storage = curState.storage(pathInputHelper, originStorage ?: ColoredStorage(version = settingsRep().newStorageVersion()))

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

    private fun updatePathVariants() = scope.launchLatest("path_variants", FSDI.defaultDispatcher()) {
        with(pathInputHelper) {
            state.value.path.text
                .pathVariables()
                .collect { pathVariants ->
                    state.update {
                        it.copy(storagePathVariants = pathVariants)
                    }
                }
        }
    }

    private fun backFromScreen(router: AppRouter?) {
        clean()
        router?.back()
    }

    private fun clean() = input { copy(isSkeleton = true) }

}