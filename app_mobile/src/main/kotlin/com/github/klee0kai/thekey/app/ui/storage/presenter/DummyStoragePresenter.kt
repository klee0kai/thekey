package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.domain.model.ColoredNote
import com.github.klee0kai.thekey.app.domain.model.dummyLazyColorGroup
import com.github.klee0kai.thekey.app.domain.model.dummyLazyColorGroupSkeleton
import com.github.klee0kai.thekey.app.domain.model.dummyLazyColoredNoteLoaded
import com.github.klee0kai.thekey.app.domain.model.dummyLazyColoredNoteSkeleton
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.utils.common.Dummy
import kotlinx.coroutines.flow.MutableStateFlow

open class DummyStoragePresenter(
    private val isSearchActive: Boolean = false,
) : StoragePresenter {

    override val searchState = MutableStateFlow(
        SearchState(
            isActive = isSearchActive,
            searchText = "some_search",
        )
    )

    override val selectedGroupId = MutableStateFlow<Long?>(null)

    override val filteredColorGroups = MutableStateFlow(
        listOf(
            dummyLazyColorGroupSkeleton(),
            dummyLazyColorGroup(ColorGroup(id = Dummy.dummyId.also { selectedGroupId.value = it }, name = "AD", keyColor = KeyColor.VIOLET)),
            dummyLazyColorGroup(ColorGroup(id = Dummy.dummyId, name = "TU", keyColor = KeyColor.TURQUOISE))
        )
    )

    override val filteredNotes = MutableStateFlow(
        listOf(
            dummyLazyColoredNoteSkeleton(),
            dummyLazyColoredNoteSkeleton(),
            dummyLazyColoredNoteLoaded(
                ColoredNote(
                    site = "some.site",
                    login = "login",
                    desc = "description",
                    group = ColorGroup(
                        id = Dummy.dummyId,
                        keyColor = KeyColor.VIOLET
                    )
                )
            ),
            dummyLazyColoredNoteLoaded(
                ColoredNote(
                    site = "some.site2",
                    login = "login2",
                    desc = "description2",
                    group = ColorGroup(
                        id = Dummy.dummyId,
                        keyColor = KeyColor.ORANGE
                    )
                )
            ),
            dummyLazyColoredNoteSkeleton(),
        )
    )

}