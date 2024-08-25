package com.github.klee0kai.thekey.app.ui.notegroup.presenter

import com.github.klee0kai.thekey.app.ui.notegroup.model.EditNoteGroupsState
import com.github.klee0kai.thekey.app.ui.notegroup.model.SelectedNote
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface EditNoteGroupsPresenter {

    val state: Flow<EditNoteGroupsState> get() = emptyFlow()

    val allNotes: Flow<List<SelectedNote>> get() = emptyFlow()

    fun input(block: EditNoteGroupsState.() -> EditNoteGroupsState): Job = Job()

    fun init(): Job = Job()

    fun save(appRouter: AppRouter?): Job = Job()

}

fun EditNoteGroupsPresenter.selectNote(ptnote: Long, selected: Boolean) = input {
    copy(
        selectedNotes = if (!selected) {
            selectedNotes.toMutableSet().apply { remove(ptnote) }
        } else {
            selectedNotes + setOf(ptnote)
        }
    )
}