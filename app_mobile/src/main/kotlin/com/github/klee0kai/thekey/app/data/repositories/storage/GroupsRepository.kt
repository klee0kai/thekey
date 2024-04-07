package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.colorGroup
import com.github.klee0kai.thekey.app.model.LazyColorGroup
import com.github.klee0kai.thekey.app.model.id
import com.github.klee0kai.thekey.app.utils.common.launchLatest
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class GroupsRepository(
    val identifier: StorageIdentifier,
) {

    val scope = DI.defaultThreadScope()
    val engine = DI.cryptStorageEngineSafeLazy(identifier)

    val groups = MutableStateFlow<List<LazyColorGroup>>(emptyList())

    fun loadGroups(forceDirty: Boolean = false) = scope.launchLatest("load_groups") {
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
                        ?: LazyColorGroup(groupLite) {
                            fullyLoadedGroups.await().firstOrNull { it.id == groupLite.id } ?: groupLite
                        }.apply {
                            dirty = forceDirty
                        }
                }


        }
    }

    fun clear() = scope.launchLatest("clear") {
        groups.update { emptyList() }
    }


}