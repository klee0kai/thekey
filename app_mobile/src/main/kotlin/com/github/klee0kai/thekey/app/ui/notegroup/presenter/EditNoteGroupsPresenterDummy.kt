@file:OptIn(DelicateCoroutinesApi::class)

package com.github.klee0kai.thekey.app.ui.notegroup.presenter

import com.github.klee0kai.thekey.app.ui.notegroup.model.EditNoteGroupsState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.app.ui.storage.model.dummy
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@DebugOnly
open class EditNoteGroupsPresenterDummy(
    val skeleton: Boolean = false,
    val count: Int = 3,
) : EditNoteGroupsPresenter {

    override val state = MutableStateFlow(
        EditNoteGroupsState(
            isSkeleton = skeleton,
            colorGroupVariants = KeyColor.selectableColorGroups,
        )
    )

    override val filteredItems = MutableStateFlow(
        buildList {
            repeat(count) {
                add(StorageItem.dummy())
            }
        },
    )

    override fun input(block: EditNoteGroupsState.() -> EditNoteGroupsState) = GlobalScope.launch {
        state.update { block(it) }
    }

}
