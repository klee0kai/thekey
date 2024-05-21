package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedColorGroup
import com.github.klee0kai.thekey.app.engine.model.colorGroup
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.ColorGroup
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class GroupsRepository(
    val identifier: StorageIdentifier,
) {

    val engine = DI.cryptStorageEngineSafeLazy(identifier)

    val groups = MutableStateFlow<List<ColorGroup>>(emptyList())

    suspend fun loadGroups() = coroutineScope {
        if (groups.value.isEmpty()) {
            groups.value = engine()
                .colorGroups()
                .map { it.colorGroup(isLoaded = false) }
        }

        groups.value = engine()
            .colorGroups(info = true)
            .map { it.colorGroup(isLoaded = true) }
    }

    suspend fun saveColorGroup(decryptedColorGroup: DecryptedColorGroup): DecryptedColorGroup? {
        return engine().saveColorGroup(decryptedColorGroup).also {
            loadGroups()
        }
    }

    suspend fun removeGroup(id: Long) {
        engine().removeColorGroup(id)
        loadGroups()
    }

    suspend fun clear() = groups.update { emptyList() }

}