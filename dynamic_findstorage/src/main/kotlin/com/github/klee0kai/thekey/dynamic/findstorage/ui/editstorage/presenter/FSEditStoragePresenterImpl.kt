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
            ).copy(
                isEditMode = false,
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

    override fun remove(router: AppRouter) = scope.launch {
        val path = originStorage?.path ?: return@launch
        storagesInteractor().deleteStorage(path)
        router.snack(R.string.storage_deleted)
        backFromScreen(router)
    }

    override fun save(router: AppRouter) = scope.launch {
        val curState = state.value
        var storage = curState.storage(pathInputHelper, originStorage ?: ColoredStorage(version = settingsRep().newStorageVersion()))

        when {
            originStorage == null -> {
                val result = interactor().createStorage(storage).await()

                if (result.isSuccess) {
                    router.snack(R.string.storage_created)
                    backFromScreen(router)
                } else {
                    router.snack(R.string.unknown_error)
                }
            }

            originStorage?.path != storage.path -> {
                val result = interactor().createStorage(storage).await()
                if (result.isSuccess) {
                    router.snack(R.string.storage_moved)
                    backFromScreen(router)
                } else {
                    router.snack(R.string.unknown_error)
                }
            }

            else -> {
                val result = interactor().setStorage(storage).await()

                if (result.isSuccess) {
                    router.snack(R.string.storage_saved)
                    backFromScreen(router)
                } else {
                    router.snack(R.string.unknown_error)
                }
            }
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

    private fun backFromScreen(router: AppRouter) {
        clean()
        router.back()
    }

    private fun clean() = input { copy(isSkeleton = true) }

}