package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class GroupsInteractor(
    val identifier: StorageIdentifier,
) {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.groupRepLazy(identifier)

    val groups = flow { rep().groups.collect(this) }

    fun loadGroups() = scope.launch {
        rep().loadGroups()
    }

    fun clearCache() = scope.launch {
        rep().clearCache()
    }

    fun removeGroup(id: Long) = scope.launch {
        rep().removeGroup(id)
    }

    fun saveColorGroup(decryptedColorGroup: DecryptedColorGroup) = scope.async {
        rep().saveColorGroup(decryptedColorGroup)
    }

}