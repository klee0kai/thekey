package com.github.klee0kai.thekey.app.ui.notegroup.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.ui.notegroup.model.EditNoteGroupsState
import com.github.klee0kai.thekey.app.ui.notegroup.model.selectedColorGroup
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.app.ui.storage.model.group
import com.github.klee0kai.thekey.app.ui.storage.presenter.StoragePresenterHelper
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.otpNotes
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.common.launchSafe
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class EditNoteGroupsPresenterImpl(
    private val groupIdentifier: NoteGroupIdentifier,
) : EditNoteGroupsPresenter {

    private val scope = DI.defaultThreadScope()
    private val interactor = DI.groupsInteractorLazy(groupIdentifier.storageIdentifier)
    private val predefinedGroupsInteractor =
        DI.predefinedNoteGroupsInteractor(groupIdentifier.storageIdentifier)
    private val notesInteractor = DI.notesInteractorLazy(groupIdentifier.storageIdentifier)
    private val otpNotesInteractor = DI.otpNotesInteractorLazy(groupIdentifier.storageIdentifier)

    private val isOtpGroupMode get() = groupIdentifier.groupId == ColorGroup.otpNotes().id

    private var originalGroup: ColorGroup? = null
    private var originSelectedNotes: Set<String> = emptySet()

    override val state = MutableStateFlow(EditNoteGroupsState(isSkeleton = true))
    override val searchState = MutableStateFlow(SearchState())

    private val sortedStorageItems = StoragePresenterHelper
        .sortedStorageItemsFlow(notesInteractor, otpNotesInteractor)
        .flowOn(DI.defaultDispatcher())

    private val selectedStorageItems = flow<List<StorageItem>> {
        combine(
            flow = sortedStorageItems,
            flow2 = state.map { it.selectedStorageItems }.distinctUntilChanged(),
            flow3 = state.map { it.isOtpGroupMode }.distinctUntilChanged(),
            transform = { items, selectList, isOtpGroupMode ->
                var storageItems = items.map { it.copy(selected = selectList.contains(it.id)) }
                if (isOtpGroupMode) storageItems = storageItems.filter { it.otp != null }
                storageItems
            }
        ).collect(this@flow)
    }.flowOn(DI.defaultDispatcher())

    override val filteredItems = flow<List<StorageItem>> {
        combine(
            flow = searchState,
            flow2 = selectedStorageItems
        ) { search, storageItems ->
            if (search.isActive) {
                storageItems.filter { it.filterBy(search.searchText) }
            } else {
                storageItems
            }
        }.collect(this)
    }.flowOn(DI.defaultDispatcher())

    override fun init() = scope.launch {
        if (!state.value.isSkeleton) {
            // add unique buttons if needed
            state.update { it.copy(isRemoveAvailable = it.isEditMode) }
            return@launch
        }

        val otpGroup = predefinedGroupsInteractor().otpNoteGroup.firstOrNull()
        val otpGroupRemoved = otpGroup?.isRemoved ?: false

        originalGroup = when {
            isOtpGroupMode -> otpGroup
            else -> interactor()
                .groups
                .firstOrNull()
                ?.firstOrNull { it.id == groupIdentifier.groupId }
        }

        val selectedNotes = sortedStorageItems
            .firstOrNull()
            ?.filter { it.group.id == originalGroup?.id }
            ?.map { it.id }
            ?.toSet()
            ?: emptySet()
        originSelectedNotes = selectedNotes


        val colorGroupVariants = when {
            otpGroupRemoved && groupIdentifier.groupId == null -> listOfNotNull(otpGroup) + KeyColor.selectableColorGroups
            else -> KeyColor.selectableColorGroups
        }

        state.value = EditNoteGroupsState(
            isSkeleton = false,
            isEditMode = originalGroup != null,
            isRemoveAvailable = originalGroup != null,
            isOtpGroupMode = originalGroup?.id == otpGroup?.id,
            colorGroupVariants = colorGroupVariants,
            selectedGroupId = colorGroupVariants
                .firstOrNull { selectable -> selectable.keyColor == originalGroup?.keyColor }
                ?.id ?: 0,
            name = originalGroup?.name ?: "",
            otpColorName = otpGroup?.name ?: "",
            selectedStorageItems = selectedNotes,
        )
    }

    override fun searchFilter(
        newParams: SearchState,
    ) = scope.launch(start = CoroutineStart.UNDISPATCHED) {
        searchState.value = newParams
    }

    override fun input(
        block: EditNoteGroupsState.() -> EditNoteGroupsState,
    ) = scope.launch(DI.mainDispatcher()) {
        val oldState = state.value
        var newState = block(oldState)
        val newKeyColor = newState.selectedColorGroup?.keyColor
        val fulfilled = newKeyColor != null && newKeyColor != KeyColor.NOCOLOR
        val isSaveAvailable = when {
            originalGroup != null -> fulfilled && (newKeyColor != originalGroup?.keyColor || originSelectedNotes != newState.selectedStorageItems)
            else -> fulfilled
        }
        newState = newState.copy(
            isSaveAvailable = isSaveAvailable,
            isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
            isOtpGroupMode = isOtpGroupMode || newState.selectedGroupId == ColorGroup.otpNotes().id,
            otpColorName = newState.otpColorName.take(3),
            name = newState.name.take(2),
        )
        state.value = newState
    }

    override fun save(router: AppRouter?) = scope.launchSafe {
        val curState = state.value
        val selectedKeyColor = curState.selectedColorGroup?.keyColor ?: return@launchSafe
        val sortedItems = sortedStorageItems.firstOrNull()

        router?.back()
        clean()

        if (curState.isOtpGroupMode) {
            predefinedGroupsInteractor()
                .setColorGroup(
                    ColorGroup(
                        id = ColorGroup.otpNotes().id,
                        name = curState.otpColorName,
                        keyColor = curState.colorGroupVariants.firstOrNull { it.id == curState.selectedGroupId }
                            ?.keyColor ?: KeyColor.NOCOLOR,
                    )
                ).join()
        } else {
            // state is cleared. using collected info
            val group = interactor()
                .saveColorGroup(
                    DecryptedColorGroup(
                        id = groupIdentifier.groupId ?: 0,
                        color = selectedKeyColor.ordinal,
                        name = curState.name
                    )
                ).await()
                ?: return@launchSafe

            val resetNotes = sortedItems
                ?.filter { it.id !in curState.selectedStorageItems }
                ?.mapNotNull { it.note }
                ?.filter { note -> note.group.id == originalGroup?.id }
                ?.map { it.id }
                ?: emptyList()

            notesInteractor().setNotesGroup(resetNotes, 0L)

            val selectNotes = sortedItems
                ?.filter { it.id in curState.selectedStorageItems }
                ?.mapNotNull { it.note?.id }
                ?: emptyList()

            notesInteractor().setNotesGroup(selectNotes, group.id)

            val resetOtpNotes = sortedItems
                ?.filter { it.id !in curState.selectedStorageItems }
                ?.mapNotNull { it.otp }
                ?.filter { otpNote -> otpNote.group.id == originalGroup?.id }
                ?.map { it.id }
                ?: emptyList()
            otpNotesInteractor().setOtpNotesGroup(resetOtpNotes, 0L)

            val selectOtpNotes = sortedItems
                ?.filter { it.id in curState.selectedStorageItems }
                ?.mapNotNull { it.otp?.id }
                ?: emptyList()

            otpNotesInteractor().setOtpNotesGroup(selectOtpNotes, group.id)
        }

        router?.snack(R.string.color_group_changed)
    }

    override fun remove(
        router: AppRouter?,
    ) = scope.launchSafe {
        val originGroup = originalGroup ?: return@launchSafe
        val sortedItems = sortedStorageItems.firstOrNull()

        router?.back()
        clean()

        if (isOtpGroupMode) {
            predefinedGroupsInteractor()
                .deleteColorGroup(id = ColorGroup.otpNotes().id)
                .join()
        } else {

            val resetNotes = sortedItems
                ?.mapNotNull { it.note }
                ?.filter { note -> note.group.id == originGroup.id }
                ?.map { it.id }
                ?: emptyList()

            notesInteractor().setNotesGroup(resetNotes, 0L)

            val resetOtpNotes = sortedItems
                ?.mapNotNull { it.otp }
                ?.filter { otpNote -> otpNote.group.id == originGroup.id }
                ?.map { it.id }
                ?: emptyList()

            otpNotesInteractor().setOtpNotesGroup(resetOtpNotes, 0L)
            interactor().removeGroup(originGroup.id).join()
        }

        router?.snack(R.string.color_group_deleted)
    }

    private fun clean() {
        state.value = EditNoteGroupsState(isSkeleton = true)
    }

}


