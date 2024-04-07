package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.domain.model.LazyColorGroup
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.flow.flow

class GroupsInteractor(
    val identifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.groupRepLazy(identifier)

    val groups = flow<List<LazyColorGroup>> {
        rep().groups.collect(this)
    }

    fun loadGroups(forceDirty: Boolean = false) = scope.launchLatest("load_groups") {
        rep().loadGroups(forceDirty)
    }

    fun clear() = scope.launchLatest("clear") {
        rep().clear()
    }


}