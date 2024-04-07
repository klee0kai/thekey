package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.colorGroup
import com.github.klee0kai.thekey.app.model.LazyColorGroup
import com.github.klee0kai.thekey.app.model.id
import com.github.klee0kai.thekey.app.utils.lazymodel.LazyModelProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class GroupsRepository(
    val identifier: StorageIdentifier,
) {

    val engine = DI.cryptStorageEngineSafeLazy(identifier)
    val groups = MutableStateFlow<List<LazyColorGroup>>(emptyList())

    suspend fun loadGroups(forceDirty: Boolean = false) = coroutineScope {
        groups.update { oldGroups ->
            val fullyLoadedGroups = async {
                engine()
                    .colorGroups(info = true)
                    .map { it.colorGroup() }
            }

            engine()
                .colorGroups()
                .map { it.colorGroup() }
                .map { groupLite ->
                    oldGroups.firstOrNull { it.id == groupLite.id }
                        ?: LazyModelProvider(groupLite) {
                            fullyLoadedGroups.await().firstOrNull { it.id == groupLite.id } ?: groupLite
                        }.apply {
                            if (forceDirty) dirty()
                        }
                }
        }
    }

    suspend fun clear() = groups.update { emptyList() }

}