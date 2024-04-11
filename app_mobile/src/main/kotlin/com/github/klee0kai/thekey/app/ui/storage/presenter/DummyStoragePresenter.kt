package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColorGroup
import com.github.klee0kai.thekey.app.domain.model.ColoredNote
import com.github.klee0kai.thekey.app.ui.designkit.color.KeyColor
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.utils.common.Dummy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class DummyStoragePresenter(
    private val isSearchActive: Boolean = false,
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
        listOf(
            ColorGroup(id = Dummy.dummyId),
            ColorGroup(id = Dummy.dummyId.also { selectedGroupId.value = it }, name = "AD", keyColor = KeyColor.VIOLET),
            ColorGroup(id = Dummy.dummyId, name = "TU", keyColor = KeyColor.TURQUOISE)
        )
    )

    override val filteredNotes = MutableStateFlow(
        listOf(
            ColoredNote(ptnote = Dummy.dummyId),
            ColoredNote(ptnote = Dummy.dummyId),
            ColoredNote(
                ptnote = Dummy.dummyId,
                site = "some.site",
                login = "login",
                desc = "description",
                group = ColorGroup(
                    id = Dummy.dummyId,
                    keyColor = KeyColor.VIOLET
                ),
                isLoaded = true,
            ),
            ColoredNote(
                ptnote = Dummy.dummyId,
                site = "some.site2",
                login = "login2",
                desc = "description2",
                group = ColorGroup(
                    id = Dummy.dummyId,
                    keyColor = KeyColor.ORANGE
                ),
                isLoaded = true,
            ),
            ColoredNote(ptnote = Dummy.dummyId),
        )
    )

    init {
        scope.launch {
            delay(1000)

            filteredNotes.update {
                it.map { it.copy(isLoaded = true) }
            }

        }
    }

}