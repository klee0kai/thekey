package com.github.klee0kai.thekey.app.ui.storagegroup.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.storagegroup.model.EditStorageGroupsState
import com.github.klee0kai.thekey.app.ui.storagegroup.model.colorGroup
import com.github.klee0kai.thekey.app.ui.storagegroup.model.selected
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.common.launchSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class EditStoragesGroupsPresenterImpl(
    val groupIdentifier: StorageGroupIdentifier,
) : EditStoragesGroupPresenter {

    private val router = DI.router()
    private val scope = DI.defaultThreadScope()
    private val rep = DI.storagesRepositoryLazy()

    override val state = MutableStateFlow(EditStorageGroupsState(isSkeleton = true))

    override val allStorages = flow {
        combine(
            flow = rep().allStorages,
            flow2 = state.map { it.selectedStorages }.distinctUntilChanged()
        ) { storages, selectList ->
            storages.map { it.selected(selected = selectList.contains(it.path)) }
        }.collect(this)
    }

    private var originalGroup: ColorGroup? = null

    override fun init() = scope.launch {
        if (!state.value.isSkeleton) return@launch

        originalGroup = rep().allColorGroups
            .firstOrNull()
            ?.firstOrNull { it.id == groupIdentifier.groupId }

        val selectedStorages = rep()
            .allStorages
            .firstOrNull()
            ?.filter { it.colorGroup?.id != null && it.colorGroup?.id == originalGroup?.id }
            ?.map { it.path }
            ?: emptyList()

        state.update {
            it.copy(
                isSkeleton = false,
                isEditMode = originalGroup != null,
                color = originalGroup?.keyColor ?: KeyColor.NOCOLOR,
                name = originalGroup?.name ?: "",
                isFavorite = originalGroup?.isFavorite ?: false,
                selectedStorages = selectedStorages.toSet()
            )
        }
    }

    override fun input(block: EditStorageGroupsState.() -> EditStorageGroupsState) = scope.launch(DI.mainDispatcher()) {
        var newState = block.invoke(state.value)
        val isSaveAvailable = when {
            originalGroup != null -> newState.color != KeyColor.NOCOLOR && newState.colorGroup(originalGroup!!) != originalGroup
            else -> newState.color != KeyColor.NOCOLOR
        }
        newState = newState.copy(
            isSaveAvailable = isSaveAvailable,
            isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
        )
        state.value = newState
    }

    override fun save() = scope.launchSafe {
        val curState = state.value
        if (curState.color == KeyColor.NOCOLOR) {
            router.snack(R.string.select_color)
            return@launchSafe
        }
        val group = rep()
            .setColorGroup(curState.colorGroup(originalGroup ?: ColorGroup()))
            .await()

        val selectedStorages = curState.selectedStorages
        val resetNotes = allStorages.firstOrNull()
            ?.filter { note -> note.group?.id == originalGroup?.id && note.path !in selectedStorages }
            ?.map { it.path }
            ?: emptyList()
        rep().setStoragesGroup(resetNotes, 0)
        rep().setStoragesGroup(selectedStorages.toList(), group.id)

        router.snack(R.string.save_success)
        router.back()
        clean()
    }

    override fun remove() = scope.launchSafe {
        val originGroup = originalGroup ?: return@launchSafe
        rep().deleteColorGroup(originGroup.id)
        router.snack(R.string.color_group_deleted)
        router.back()
        clean()
    }

    private fun clean() = input { copy(isSkeleton = true) }

}


