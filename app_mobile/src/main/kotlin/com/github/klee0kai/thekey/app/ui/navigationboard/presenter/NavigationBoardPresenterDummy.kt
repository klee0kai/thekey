package com.github.klee0kai.thekey.app.ui.navigationboard.presenter

import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

class NavigationBoardPresenterDummy(
    val hasCurrentStorage: Boolean = false,
    val hasFavorites: Boolean = false,
    val hasOpened: Boolean = false,
) : NavigationBoardPresenter {

    override val currentStorage = when {
        hasCurrentStorage -> MutableStateFlow(
            ColoredStorage(path = "phoneStorage/Documents/pet.ckey", name = "petprojects")
        )

        else -> emptyFlow()
    }

    override val openedStoragesFlow = when {
        hasOpened -> MutableStateFlow(
            listOf(
                ColoredStorage(path = "phoneStorage/Documents/cryp_storagess.ckey", name = "petprojects"),
                ColoredStorage(path = "phoneStorage/Documents/business.ckey", name = "business"),
                ColoredStorage(path = "phoneStorage/Documents/work_storage.ckey", name = "work accounts"),
            )
        )

        else -> emptyFlow()
    }

    override val favoritesStorages = when {
        hasFavorites -> MutableStateFlow(
            listOf(
                ColoredStorage(path = "phoneStorage/Documents/cryp_storagess.ckey", name = "petprojects"),
                ColoredStorage(path = "phoneStorage/Documents/business.ckey", name = "business"),
                ColoredStorage(path = "phoneStorage/Documents/work_storage.ckey", name = "work accounts"),
            )
        )

        else -> emptyFlow()
    }
}