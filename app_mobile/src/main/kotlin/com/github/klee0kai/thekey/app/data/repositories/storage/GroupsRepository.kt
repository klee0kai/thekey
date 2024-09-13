package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.engine.model.colorGroup
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.utils.coroutine.lazyStateFlow

class GroupsRepository(
    val identifier: StorageIdentifier,
) {

    private val engine = DI.cryptStorageEngineSafeLazy(identifier)
    private val scope = DI.defaultThreadScope()

    val groups = lazyStateFlow(
        init = emptyList<ColorGroup>(),
        defaultArg = false,
        scope = scope
    ) { force ->
        if (value.isNotEmpty() && !force) return@lazyStateFlow

        if (value.isEmpty()) {
            value = engine()
                .colorGroups()
                .map { it.colorGroup(isLoaded = false) }
        }

        value = engine()
            .colorGroups(info = true)
            .map { it.colorGroup(isLoaded = true) }
    }

    suspend fun saveColorGroup(decryptedColorGroup: DecryptedColorGroup): DecryptedColorGroup? {
        val result = engine().saveColorGroup(decryptedColorGroup)
        groups.touch(true)
        return result
    }

    suspend fun removeGroup(id: Long) {
        engine().removeColorGroup(id)
        groups.touch(true)
    }

    suspend fun clearCache() {
        groups.value = emptyList()
    }

}