package com.github.klee0kai.thekey.app.ui.storage.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.app.ui.storage.model.SearchState
import com.github.klee0kai.thekey.app.ui.storage.model.storageItem
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.common.Dummy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class StoragePresenterDummy(
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

    override val filteredItems = MutableStateFlow(
        listOf(
            ColoredNote(id = Dummy.dummyId).storageItem(),
            ColoredNote(id = Dummy.dummyId).storageItem(),
            ColoredOtpNote(id = Dummy.dummyId).storageItem(),
            ColoredNote(
                id = Dummy.dummyId,
                site = "some.site",
                login = "login",
                desc = "description",
                group = ColorGroup(
                    id = Dummy.dummyId,
                    keyColor = KeyColor.VIOLET
                ),
                isLoaded = true,
            ).storageItem(),
            ColoredNote(
                id = Dummy.dummyId,
                site = "some.site2",
                login = "login2",
                desc = "description2",
                group = ColorGroup(
                    id = Dummy.dummyId,
                    keyColor = KeyColor.ORANGE
                ),
                isLoaded = true,
            ).storageItem(),
            ColoredOtpNote(
                id = Dummy.dummyId,
                issuer = "Example@otp.su",
                name = "ExampleName",
                group = ColorGroup(
                    id = Dummy.dummyId,
                    keyColor = KeyColor.ORANGE
                ),
                isLoaded = true,
            ).storageItem(),

            ColoredNote(id = Dummy.dummyId).storageItem(),
        )
    )

    init {
        scope.launch {
            delay(1000)

            filteredItems.update {
                it.map { storageItem ->
                    storageItem.copy(
                        note = storageItem.note?.copy(isLoaded = true),
                        otp = storageItem.otp?.copy(isLoaded = true),
                    )
                }
            }
        }
    }

}