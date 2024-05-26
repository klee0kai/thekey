package com.github.klee0kai.thekey.app.ui.storages.presenter

import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import kotlinx.coroutines.flow.MutableStateFlow

open class StoragesPresenterDummy : StoragesPresenter {

    override val filteredStorages = MutableStateFlow(
        listOf(
            ColoredStorage(path = "appFolder/social.ckey", name = "social", description = "social sites storage"),
            ColoredStorage(path = "appFolder/work.ckey", name = "work", description = "work key storage"),
        )
    )

}