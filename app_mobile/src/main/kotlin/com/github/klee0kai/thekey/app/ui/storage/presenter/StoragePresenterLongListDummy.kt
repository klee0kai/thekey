package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.domain.model.ColoredNote
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.storageItem
import com.github.klee0kai.thekey.core.utils.common.Dummy
import kotlinx.coroutines.flow.MutableStateFlow

open class StoragePresenterLongListDummy(
    private val isSearchActive: Boolean = false,
    private val groupsCount: Int = 1,
    private val notesCount: Int = 1,
) : StoragePresenter {

    private val scope = DI.defaultThreadScope()

    override val searchState = MutableStateFlow(
        SearchState(
            isActive = isSearchActive,
            searchText = "some_search",
        )
    )

    override val selectedGroupId = MutableStateFlow<Long?>(null)

    override val filteredColorGroups = MutableStateFlow(
        buildList {
            repeat(groupsCount) {
                add(ColorGroup(id = Dummy.dummyId, name = "G${it}", keyColor = KeyColor.TURQUOISE))
            }
        }
    )

    override val filteredItems = MutableStateFlow(
        buildList {
            repeat(notesCount) {
                add(
                    ColoredNote(
                        ptnote = Dummy.dummyId,
                        site = "some${it}.site",
                        login = "login${it}",
                        desc = "description${it}",
                        group = ColorGroup(
                            id = Dummy.dummyId,
                            keyColor = KeyColor.colors.random()
                        ),
                        isLoaded = true,
                    ).storageItem(),
                )
            }
        }
    )


}