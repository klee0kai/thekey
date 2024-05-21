@file:OptIn(DelicateCoroutinesApi::class)

package com.github.klee0kai.thekey.app.ui.notegroup.presenter

import com.github.klee0kai.thekey.app.ui.notegroup.model.EditNoteGroupsState
import com.github.klee0kai.thekey.app.ui.notegroup.model.SelectedNote
import com.github.klee0kai.thekey.core.utils.common.Dummy
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class EditNoteGroupsPresenterDummy(
    val skeleton: Boolean = false,
) : EditNoteGroupsPresenter {

    override val state = MutableStateFlow(
        EditNoteGroupsState(
            isSkeleton = skeleton,
        )
    )
    override val allNotes = MutableStateFlow(
        listOf(
            SelectedNote(
                ptnote = Dummy.dummyId,
                site = "some.site.com",
                login = "SE1Logjn",
                selected = false,
            ),
            SelectedNote(
                ptnote = Dummy.dummyId,
                site = "any.com",
                login = "L2Din",
                selected = true,
            )
        ),
    )

    override fun input(block: EditNoteGroupsState.() -> EditNoteGroupsState) = GlobalScope.launch {
        state.update { block(it) }
    }

}
