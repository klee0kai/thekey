package com.github.klee0kai.thekey.app.ui.storages.presenter

import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.feature.model.NotInstalled
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.common.Dummy
import kotlinx.coroutines.flow.MutableStateFlow

open class StoragesPresenterDummy(
    val groupsCount: Int = 3,
    val storagesCount: Int = 4,
) : StoragesPresenter {

    override val installAutoSearchStatus = MutableStateFlow(NotInstalled)

    override val selectableColorGroups = MutableStateFlow<List<ColorGroup>>(
        buildList {
            repeat(groupsCount) { index ->
                add(
                    ColorGroup(
                        id = Dummy.dummyId,
                        name = "A${index}",
                        keyColor = KeyColor.colors.random(),
                    )
                )
            }
        }
    )

    override val filteredColorGroups = selectableColorGroups

    override val filteredStorages = MutableStateFlow(
        buildList {
            repeat(storagesCount) { index ->
                add(
                    ColoredStorage(
                        path = "appFolder/storage${index}.ckey",
                        name = "social-${index}",
                        description = "social sites storage",
                        version = 1,
                    ),
                )
            }
        }
    )

}