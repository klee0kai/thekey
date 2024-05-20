package com.github.klee0kai.thekey.app.ui.notegroup.presenter

import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.ui.notegroup.model.EditNoteGroupsState
import com.github.klee0kai.thekey.app.ui.notegroup.model.SelectedNote
import com.github.klee0kai.thekey.app.ui.notegroup.model.selected
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.utils.common.launchLatest
import com.github.klee0kai.thekey.core.utils.common.launchSafe
import com.github.klee0kai.thekey.core.utils.coroutine.triggerOn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class EditNoteGroupsPresenterImpl(
    val groupIdentifier: NoteGroupIdentifier,
) : EditNoteGroupsPresenter {

    private val router = DI.router()
    private val scope = DI.defaultThreadScope()
    private val interactor = DI.groupsInteractorLazy(groupIdentifier.storageIdentifier)
    private val notesInteractor = DI.notesInteractorLazy(groupIdentifier.storageIdentifier)

    override val state = MutableStateFlow(EditNoteGroupsState(isSkeleton = true))
    override val allNotes = flow<List<SelectedNote>> {
        notesInteractor().notes
            .triggerOn(updateNoteTrigger)
            .map { notes ->
                val selected = state.value.selectedNotes
                notes.filter { note ->
                    note.group.id in listOf(0L, originalGroup?.id ?: 0L)
                }.map { note ->
                    note.selected(selected.contains(note.ptnote))
                }
            }
            .collect(this)
    }.flowOn(DI.defaultDispatcher())

    private val updateNoteTrigger = MutableSharedFlow<Unit>()
    private var originalGroup: ColorGroup? = null

    override fun init() = scope.launchLatest("init") {
        if (groupIdentifier.groupId != null) {
            state.update { it.copy(isEditMode = true, isSkeleton = true) }

            originalGroup = interactor().groups
                .firstOrNull()
                ?.firstOrNull { it.id == groupIdentifier.groupId }
                ?: return@launchLatest

            notesInteractor().notes
                .firstOrNull()
                ?.filter { it.group.id == originalGroup?.id }
                ?.map { it.ptnote }
                ?.let { selectedNotes -> input { copy(selectedNotes = selectedNotes.toSet()) } }

            updateNoteTrigger.emit(Unit)

            state.update {
                it.copy(
                    isSkeleton = false,
                    color = originalGroup?.keyColor ?: KeyColor.NOCOLOR,
                    name = originalGroup?.name ?: ""
                )
            }
        } else {
            state.update { it.copy(isSkeleton = false, isEditMode = false) }
        }
    }

    override fun input(block: EditNoteGroupsState.() -> EditNoteGroupsState) = scope.launch {
        state.update { oldState ->
            val newState = block(oldState)
            updateNoteTrigger.emit(Unit)
            newState
        }
    }

    override fun save() = scope.launchSafe {
        val curState = state.value
        if (curState.color == KeyColor.NOCOLOR) {
            router.snack(R.string.select_color)
            return@launchSafe
        }
        val group = interactor().saveColorGroup(
            DecryptedColorGroup(
                id = groupIdentifier.groupId ?: 0,
                color = curState.color.ordinal,
                name = curState.name
            )
        ).await() ?: return@launchSafe

        val selectNotes = curState.selectedNotes
        val resetNotes = allNotes.firstOrNull()
            ?.filter { note -> note.group.id == originalGroup?.id && note.ptnote !in selectNotes }
            ?.map { it.ptnote }
            ?: emptyList()

        notesInteractor().setNotesGroup(selectNotes.toList(), group.id)
        notesInteractor().setNotesGroup(resetNotes, 0L)

        router.back()
    }
}


