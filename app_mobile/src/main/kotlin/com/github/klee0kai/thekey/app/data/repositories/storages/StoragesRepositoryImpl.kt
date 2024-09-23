package com.github.klee0kai.thekey.app.data.repositories.storages

import com.github.klee0kai.thekey.app.data.mapping.toColoredStorage
import com.github.klee0kai.thekey.app.data.mapping.toStorageEntry
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.data.room.entry.toColorGroup
import com.github.klee0kai.thekey.core.data.room.entry.toColorGroupEntry
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.utils.coroutine.lazyStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class StoragesRepositoryImpl : StoragesRepository {

    private val storagesDao = DI.storageDaoLazy()
    private val colorGroupsDao = DI.colorGroupDaoLazy()
    private val scope = DI.ioThreadScope()

    override val allColorGroups = lazyStateFlow(
        init = emptyList<ColorGroup>(),
        defaultArg = false,
        scope = scope,
    ) {
        value = colorGroupsDao()
            .getAll()
            .map { it.toColorGroup() }
    }

    override val allStorages = lazyStateFlow(
        init = emptyList<ColoredStorage>(),
        defaultArg = false,
        scope = scope,
    ) {
        val allColorGroups = colorGroupsDao()
            .getAll()
            .map { it.toColorGroup() }

        value = storagesDao()
            .getAll()
            .map { entry ->
                entry.toColoredStorage()
                    .copy(
                        colorGroup = allColorGroups
                            .firstOrNull { group -> entry.coloredGroupId == group.id },
                    )
            }
    }

    override fun setColorGroup(colorGroup: ColorGroup) = scope.async {
        val id = colorGroupsDao().update(colorGroup.toColorGroupEntry())
        allColorGroups.touch(true)
        colorGroupsDao()[id]?.toColorGroup() ?: ColorGroup()
    }

    override fun deleteColorGroup(id: Long): Job = scope.launch {
        colorGroupsDao().delete(id)
        allColorGroups.touch(true)
    }

    override fun findStorage(path: String) = scope.async {
        val storage = storagesDao().get(path) ?: return@async null
        val colorGroup = colorGroupsDao()[storage.coloredGroupId]
        storage.toColoredStorage().copy(colorGroup = colorGroup?.toColorGroup())
    }

    override fun setStorage(storage: ColoredStorage): Job = scope.launch {
        if (storage.version <= 0) Timber.e("incorrect storage version ${storage.version}")

        val cachedStorage = storagesDao().get(storage.path)
        storagesDao().update(storage.toStorageEntry(id = cachedStorage?.id))
        allStorages.touch(true)
    }

    override fun setStoragesGroup(storagePaths: List<String>, groupId: Long) = scope.launch {
        storagePaths.forEach { path ->
            val cachedStorage = storagesDao().get(path) ?: return@forEach
            storagesDao().update(cachedStorage.copy(coloredGroupId = groupId))
        }
        allStorages.touch(true)
    }

    override fun deleteStorage(path: String): Job = scope.launch {
        storagesDao().delete(path)
        allStorages.touch(true)
    }

}