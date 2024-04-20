package com.github.klee0kai.thekey.app.ui.navigationboard.presenter

import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface NavigationBoardPresenter {

    val currentStorage: Flow<ColoredStorage?> get() = emptyFlow()

    val openedStoragesFlow: Flow<List<ColoredStorage>> get() = emptyFlow()

    val favoritesStorages: Flow<List<ColoredStorage>> get() = emptyFlow()

}