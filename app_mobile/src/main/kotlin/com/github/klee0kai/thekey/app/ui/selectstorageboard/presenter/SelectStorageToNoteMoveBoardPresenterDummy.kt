package com.github.klee0kai.thekey.app.ui.selectstorageboard.presenter

import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.dummy
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(DebugOnly::class)
class SelectStorageToNoteMoveBoardPresenterDummy(
    val hasCurrentStorage: Boolean = false,
    val opennedCount: Int = 3,
) : SelectStorageToNoteMoveBoardPresenter {

    override val currentStorage = when {
        hasCurrentStorage -> MutableStateFlow(ColoredStorage.dummy())
        else -> emptyFlow()
    }

    override val openedStoragesFlow = MutableStateFlow(
        buildList { repeat(opennedCount) { add(ColoredStorage.dummy()) } }
    )
}