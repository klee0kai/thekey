package com.github.klee0kai.thekey.app.ui.storagegroup.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.storagegroup.model.EditStorageGroupsState
import com.github.klee0kai.thekey.app.ui.storagegroup.model.colorGroup
import com.github.klee0kai.thekey.app.ui.storagegroup.model.selected
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.externalStorages
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.common.launchSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
    private val interactor = DI.storagesInteractorLazy()
    private val settings = DI.settingsRepositoryLazy()

    private val isExternalGroupMode get() = groupIdentifier.groupId == ColorGroup.externalStorages().id

    override val state = MutableStateFlow(EditStorageGroupsState(isSkeleton = true))
    private val shortPath = DI.userShortPaths()

    override val allStorages = flow {
        combine(
            flow = interactor().allStorages,
            flow2 = state.map { it.selectedStorages }.distinctUntilChanged(),
            flow3 = state.map { it.selectedGroupId }.distinctUntilChanged(),
        ) { storages, selectList, selectedGroupId ->
            when {
                isExternalGroupMode || selectedGroupId == ColorGroup.externalStorages().id -> storages.filter { shortPath.isExternal(it.path) }
                else -> storages
            }.map { it.selected(selected = selectList.contains(it.path)) }
        }.collect(this)
    }

    private var originalGroup: ColorGroup? = null
    private var originSelectedStorages: List<String> = emptyList()

    override fun init() = scope.launch {
        if (!state.value.isSkeleton) return@launch

        val externalGroupRemoved = !settings().externalStoragesGroup()
        val externalsGroup = interactor().externalStoragesGroup.first()
        originalGroup = when {
            isExternalGroupMode -> externalsGroup
            else -> interactor().allColorGroups
                .firstOrNull()
                ?.firstOrNull { it.id == groupIdentifier.groupId }
        }

        val selectedStorages = interactor()
            .allStorages
            .firstOrNull()
            ?.filter { it.colorGroup?.id != null && it.colorGroup?.id == originalGroup?.id }
            ?.map { it.path }
            ?: emptyList()

        originSelectedStorages = selectedStorages

        val colorGroupVariants = when {
            externalGroupRemoved && groupIdentifier.groupId == null -> listOf(externalsGroup) + KeyColor.selectableColorGroups
            else -> KeyColor.selectableColorGroups
        }
        state.update {
            it.copy(
                isSkeleton = false,
                isEditMode = originalGroup != null,
                isRemoveAvailable = originalGroup != null,
                isExternalGroupMode = originalGroup?.id == externalsGroup.id,
                colorGroupVariants = colorGroupVariants,
                selectedGroupId = colorGroupVariants.firstOrNull { selectable -> selectable.keyColor == originalGroup?.keyColor }?.id ?: 0,
                name = originalGroup?.name ?: "",
                extStorageName = externalsGroup.name,
                isFavorite = originalGroup?.isFavorite ?: false,
                selectedStorages = selectedStorages.toSet()
            )
        }
    }

    override fun input(block: EditStorageGroupsState.() -> EditStorageGroupsState) = scope.launch(DI.mainDispatcher()) {
        var newState = block.invoke(state.value)

        val newColorGroup = newState.colorGroup(originalGroup ?: ColorGroup())
        val fulfilled = newColorGroup.keyColor != KeyColor.NOCOLOR
        val isSaveAvailable = when {
            originalGroup != null -> fulfilled && (newColorGroup != originalGroup || originSelectedStorages != newState.selectedStorages)
            else -> fulfilled
        }
        newState = newState.copy(
            isSaveAvailable = isSaveAvailable,
            isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
            isExternalGroupMode = isExternalGroupMode || newState.selectedGroupId == ColorGroup.externalStorages().id,
        )
        state.value = newState
    }

    override fun save() = scope.launchSafe {
        val curState = state.value
        val ext = ColorGroup.externalStorages()
        val newColorGroup = curState.colorGroup(
            origin = originalGroup ?: ColorGroup(),
            isExtMode = isExternalGroupMode || curState.selectedGroupId == ext.id
        )
        if (newColorGroup.keyColor == KeyColor.NOCOLOR) {
            router.snack(R.string.select_color)
            return@launchSafe
        }
        if (isExternalGroupMode) {
            interactor()
                .setColorGroup(newColorGroup)
                .await()
        } else {
            val group = interactor()
                .setColorGroup(newColorGroup)
                .await()

            val selectedStorages = curState.selectedStorages
            val resetNotes = allStorages.firstOrNull()
                ?.filter { note -> note.group?.id == originalGroup?.id && note.path !in selectedStorages }
                ?.map { it.path }
                ?: emptyList()
            interactor().setStoragesGroup(resetNotes, 0)
            interactor().setStoragesGroup(selectedStorages.toList(), group.id)
        }

        router.snack(R.string.save_success)
        router.back()
        clean()
    }

    override fun remove() = scope.launchSafe {
        val originGroup = originalGroup ?: return@launchSafe
        interactor().deleteColorGroup(originGroup.id)
        router.snack(R.string.color_group_deleted)
        router.back()
        clean()
    }

    private fun clean() = input { copy(isSkeleton = true) }

}


