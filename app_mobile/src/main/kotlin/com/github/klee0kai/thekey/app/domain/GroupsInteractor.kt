package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.domain.model.LazyColorGroup
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class GroupsInteractor(
    val identifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.groupRepLazy(identifier)

    val groups = flow<List<LazyColorGroup>> {
        rep().groups.collect(this)
    }

    fun loadGroups(forceDirty: Boolean = false) = scope.launch {
        rep().loadGroups(forceDirty)
    }

    fun clear() = scope.launch {
        rep().clear()
    }

    fun removeGroup(id: Long) = scope.launch {
        rep().removeGroup(id)
    }

    fun saveColorGroup(decryptedColorGroup: DecryptedColorGroup) = scope.async {
        rep().saveColorGroup(decryptedColorGroup)
    }

}