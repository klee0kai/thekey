package com.github.klee0kai.thekey.app.data.repositories

import com.github.klee0kai.thekey.app.data.room.entry.toColorGroup
import com.github.klee0kai.thekey.app.data.room.entry.toColorGroupEntry
import com.github.klee0kai.thekey.app.data.room.entry.toColoredStorage
import com.github.klee0kai.thekey.app.data.room.entry.toStorageEntry
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.core.domain.ColorGroup
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class FoundStoragesRepository {

    private val storagesDao = DI.storageDaoLazy()
    private val colorGroupsDao = DI.colorGroupDaoLazy()
    private val scope = DI.ioThreadScope()

    val updateDbFlow = MutableSharedFlow<Unit>()

    fun getAllColorGroups() = scope.async {
        colorGroupsDao().getAll().map { it.toColorGroup() }
    }

    fun addColorGroup(colorGroup: ColorGroup) = scope.launch {
        colorGroupsDao().update(colorGroup.toColorGroupEntry())
    }

    fun getStorages() = scope.async {
        storagesDao().getAll().map { entry -> entry.toColoredStorage() }
    }

    fun findStorage(path: String) = scope.async {
        storagesDao().get(path)?.toColoredStorage()
    }

    fun deleteStorage(path: String) = scope.launch {
        File(path).deleteOnExit()
        storagesDao().delete(path)
        updateDbFlow.emit(Unit)
    }

    fun addStorage(storage: Storage) = scope.launch {
        val cachedStorage = storagesDao().get(storage.path)
        storagesDao().insert(storage.toStorageEntry(id = cachedStorage?.id))
        updateDbFlow.emit(Unit)
    }

}