package com.github.klee0kai.thekey.app.ui.notegroup.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.ui.notegroup.model.EditNoteGroupsState
import com.github.klee0kai.thekey.app.ui.notegroup.model.selected
import com.github.klee0kai.thekey.app.ui.notegroup.model.selectedColorGroup
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.common.launchSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

open class EditNoteGroupsPresenterImpl(
    private val groupIdentifier: NoteGroupIdentifier,
) : EditNoteGroupsPresenter {

    private val scope = DI.defaultThreadScope()
    private val interactor = DI.groupsInteractorLazy(groupIdentifier.storageIdentifier)
    private val notesInteractor = DI.notesInteractorLazy(groupIdentifier.storageIdentifier)

    override val state = MutableStateFlow(EditNoteGroupsState(isSkeleton = true))

    override val allNotes = combine(
        flow = flow { notesInteractor().notes.collect(this) },
        flow2 = state.map { it.selectedNotes }.distinctUntilChanged(),
    ) { notes, selected ->
        notes.filter { note ->
            note.group.id in listOf(0L, originalGroup?.id ?: 0L)
        }.map { note ->
            note.selected(selected.contains(note.ptnote))
        }
    }.flowOn(DI.defaultDispatcher())

    private var originalGroup: ColorGroup? = null
    private var originSelectedNotes: Set<Long> = emptySet()

    override fun init() = scope.launch {
        if (!state.value.isSkeleton) {
            // add unique buttons if needed
            state.update { it.copy(isRemoveAvailable = it.isEditMode) }
            return@launch
        }

        originalGroup = interactor()
            .groups
            .firstOrNull()
            ?.firstOrNull { it.id == groupIdentifier.groupId }

        val selectedNotes = notesInteractor().notes
            .firstOrNull()
            ?.filter { it.group.id == originalGroup?.id }
            ?.map { it.ptnote }
            ?.toSet()
            ?: emptySet()
        originSelectedNotes = selectedNotes


        val colorGroupVariants = when {
//  todo QRCODE externalGroupRemoved && groupIdentifier.groupId == null -> listOf(externalsGroup) + KeyColor.selectableColorGroups
            else -> KeyColor.selectableColorGroups
        }

        state.value = EditNoteGroupsState(
            isSkeleton = false,
            isEditMode = originalGroup != null,
            isRemoveAvailable = originalGroup != null,
            colorGroupVariants = colorGroupVariants,
            selectedGroupId = colorGroupVariants
                .firstOrNull { selectable -> selectable.keyColor == originalGroup?.keyColor }
                ?.id ?: 0,
            name = originalGroup?.name ?: "",
            selectedNotes = selectedNotes,
        )
    }

    override fun input(
        block: EditNoteGroupsState.() -> EditNoteGroupsState,
    ) = scope.launch(DI.mainDispatcher()) {
        val oldState = state.value
        var newState = block(oldState)
        val newKeyColor = newState.selectedColorGroup?.keyColor
        val fulfilled = newKeyColor != null && newKeyColor != KeyColor.NOCOLOR
        val isSaveAvailable = when {
            originalGroup != null -> fulfilled && (newKeyColor != originalGroup?.keyColor || originSelectedNotes != newState.selectedNotes)
            else -> fulfilled
        }
        newState = newState.copy(
            isSaveAvailable = isSaveAvailable,
            isRemoveAvailable = newState.isRemoveAvailable && !isSaveAvailable,
        )
        state.value = newState
    }

    override fun save(router: AppRouter?) = scope.launchSafe {
        val curState = state.value
        val selectedKeyColor = curState.selectedColorGroup?.keyColor ?: return@launchSafe
        router?.back()
        clean()

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

        val selectNotes = curState.selectedNotes
        val resetNotes = allNotes.firstOrNull()
            ?.filter { note -> note.group.id == originalGroup?.id && note.ptnote !in selectNotes }
            ?.map { it.ptnote }
            ?: emptyList()

        notesInteractor().setNotesGroup(selectNotes.toList(), group.id)
        notesInteractor().setNotesGroup(resetNotes, 0L)
        router?.snack(R.string.color_group_changed)
    }

    override fun remove(router: AppRouter?) = scope.launchSafe {
        val originGroup = originalGroup ?: return@launchSafe

        router?.back()
        clean()

        val resetNotes = allNotes.firstOrNull()
            ?.filter { note -> note.group.id == originGroup.id }
            ?.map { it.ptnote }
            ?: emptyList()

        interactor().removeGroup(originGroup.id).join()
        notesInteractor().setNotesGroup(resetNotes, 0L).join()
        router?.snack(R.string.color_group_deleted)
    }

    private fun clean() {
        state.value = EditNoteGroupsState(isSkeleton = true)
    }

}


