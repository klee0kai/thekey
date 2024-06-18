package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.helpers.path.appendTKeyFormat
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.noGroup
import com.github.klee0kai.thekey.core.utils.common.launchLatest
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.FSEditStorageState
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.storage
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.updateWith
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class FSEditStoragePresenterImpl(
    val storageIdentifier: StorageIdentifier?,
) : FSEditStoragePresenter {

    private val scope = FSDI.defaultThreadScope()
    private val router = FSDI.router()
    private val rep = FSDI.storagesRepositoryLazy()
    private val settingsRep = FSDI.settingsRepositoryLazy()
    private val pathInputHelper = FSDI.pathInputHelper()
    private val appFolder by lazy { DI.ctx().applicationInfo.dataDir }

    private var originStorage: ColoredStorage? = null
    private var colorGroups: List<ColorGroup> = emptyList()

    override val state = MutableStateFlow(FSEditStorageState(isSkeleton = true))


    override fun init() = scope.launch {
        if (!state.value.isSkeleton) return@launch

        val colorGroupUpdate = launch {
            colorGroups = rep().allColorGroups
                .first()
                .let { listOf(ColorGroup.noGroup()) + it }
        }

        originStorage = storageIdentifier?.path?.let {
            rep().findStorage(it).await()
        }

        val initState = FSEditStorageState(
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

    override fun input(block: FSEditStorageState.() -> FSEditStorageState) = scope.launch(DI.mainDispatcher()) {
        state.update {
            var newState = block(it)
            val isSaveAvailable = when {
                originStorage != null -> newState.name.isNotBlank() && newState.storage(originStorage!!) != originStorage
                else -> newState.name.isNotBlank()
            }
            newState = newState.copy(
                isSaveAvailable = isSaveAvailable,
                isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
            )

            newState
        }

        updatePathVariants()
    }


    override fun remove() = scope.launch {
        val path = originStorage?.path ?: return@launch
        rep().deleteStorage(path)
        router.snack(R.string.storage_deleted)
        router.back()
        clean()
    }

    override fun save() = scope.launch {
        val curState = state.value
        var storage = curState.storage(originStorage ?: ColoredStorage(version = settingsRep().newStorageVersion()))
        if (storage.path.isBlank()) {
            storage = storage.copy(
                path = File(appFolder, curState.name)
                    .absolutePath
                    .appendTKeyFormat()
            )
        }

        rep().setStorage(storage)
        router.snack(R.string.storage_saved)
        router.back()
        clean()
    }

    private fun updatePathVariants() = scope.launchLatest("path_variants", FSDI.defaultDispatcher()) {
        with(pathInputHelper) {
            state.value.pathNoExt.text
                .pathVariables()
                .collect { pathVariants ->
                    state.update {
                        it.copy(storagePathVariants = pathVariants)
                    }
                }
        }
    }

    private fun clean() = input { copy(isSkeleton = true) }

}