@file:OptIn(DelicateCoroutinesApi::class)

package com.github.klee0kai.thekey.app.ui.storagegroup.presenter

import com.github.klee0kai.thekey.app.ui.storagegroup.model.EditStorageGroupsState
import com.github.klee0kai.thekey.app.ui.storagegroup.model.SelectedStorage
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class EditStoragesGroupPresenterDummy(
    val storagesCount: Int = 3,
) : EditStoragesGroupPresenter {

    override val variants = MutableStateFlow(
        KeyColor.colors.map {
            ColorGroup(keyColor = it)
        }
    )

    override val state = MutableStateFlow(
        EditStorageGroupsState(
            isSkeleton = false,
        )
    )

    override val allStorages: Flow<List<SelectedStorage>> = MutableStateFlow(
        buildList {
            repeat(storagesCount) { index ->
                add(
                    SelectedStorage(
                        path = "path${index}",
                        name = "name-${index}",
                        selected = index == 2,
                    )
                )
            }
        }
    )

    override fun input(block: EditStorageGroupsState.() -> EditStorageGroupsState) = GlobalScope.launch {
        state.update { block(it) }
    }

}
