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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class EditStoragePresenterImpl(
    val storageIdentifier: StorageIdentifier?,
) : EditStoragePresenter {

    private val scope = DI.defaultThreadScope()
    private val router = DI.router()
    private val rep = DI.storagesRepositoryLazy()
    private val settingsRep = DI.settingsRepositoryLazy()
    private val appFolder by lazy { DI.ctx().applicationInfo.dataDir }

    private var originStorage: ColoredStorage? = null
    private var colorGroups: List<ColorGroup> = emptyList()

    override val state = MutableStateFlow(EditStorageState(isSkeleton = true))


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
        val isSaveAvailable = when {
            originStorage != null -> newState.name.isNotBlank() && newState.storage(originStorage!!) != originStorage
            else -> newState.name.isNotBlank()
        }
        newState = newState.copy(
            isSaveAvailable = isSaveAvailable,
            isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
        )
        state.value = newState
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

    private fun clean() = input { copy(isSkeleton = true) }

}