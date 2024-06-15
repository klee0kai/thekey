package com.github.klee0kai.thekey.app.data.repositories.storages

import com.github.klee0kai.thekey.app.data.mapping.toColoredStorage
import com.github.klee0kai.thekey.app.data.mapping.toStorageEntry
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.data.room.entry.toColorGroup
import com.github.klee0kai.thekey.core.data.room.entry.toColorGroupEntry
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.utils.coroutine.onTicks
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class StoragesRepositoryImpl : StoragesRepository {

    private val storagesDao = DI.storageDaoLazy()
    private val colorGroupsDao = DI.colorGroupDaoLazy()
    private val scope = DI.ioThreadScope()

    private val updateTicker = MutableSharedFlow<Unit>()

    override val allColorGroups = flow {
        updateTicker.onTicks {
            colorGroupsDao()
                .getAll()
                .map { it.toColorGroup() }
                .also { emit(it) }
        }
    }.flowOn(DI.ioDispatcher())

    override val allStorages = flow {
        updateTicker.onTicks {
            val allColorGroups = colorGroupsDao()
                .getAll()
                .map { it.toColorGroup() }

            storagesDao()
                .getAll()
                .map { entry ->
                    entry.toColoredStorage()
                        .copy(colorGroup = allColorGroups.firstOrNull { group -> entry.coloredGroupId == group.id })
                }
                .also { emit(it) }
        }
    }.flowOn(DI.ioDispatcher())

    override fun setColorGroup(colorGroup: ColorGroup) = scope.async {
        val id = colorGroupsDao().update(colorGroup.toColorGroupEntry())
        updateTicker.emit(Unit)
        colorGroupsDao()[id]?.toColorGroup() ?: ColorGroup()
    }

    override fun deleteColorGroup(id: Long): Job = scope.launch {
        colorGroupsDao().delete(id)
        updateTicker.emit(Unit)
    }

    override fun findStorage(path: String) = scope.async {
        storagesDao().get(path)?.toColoredStorage()
    }

    override fun setStorage(storage: ColoredStorage): Job = scope.launch {
        if (storage.version <= 0) Timber.e("incorrect storage version ${storage.version}")

        val cachedStorage = storagesDao().get(storage.path)
        storagesDao().update(storage.toStorageEntry(id = cachedStorage?.id))
        updateTicker.emit(Unit)
    }

    override fun setStoragesGroup(storagePaths: List<String>, groupId: Long) = scope.launch {
        storagePaths.forEach { path ->
            val cachedStorage = storagesDao().get(path) ?: return@forEach
            storagesDao().update(cachedStorage.copy(coloredGroupId = groupId))
        }
        updateTicker.emit(Unit)
    }

    override fun deleteStorage(path: String): Job = scope.launch {
        File(path).deleteOnExit()
        storagesDao().delete(path)
        updateTicker.emit(Unit)
    }

}