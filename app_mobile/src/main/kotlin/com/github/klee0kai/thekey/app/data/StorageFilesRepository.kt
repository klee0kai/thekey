package com.github.klee0kai.thekey.app.data

import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.data.room.entry.StorageFileEntry
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.Storage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class StorageFilesRepository {

    private val storagesDao by DI.storageDaoLazy()

    private val scope = DI.ioThreadScope()

    fun getStorages() = scope.async {
        storagesDao.getAll().mapNotNull { entry ->
            if (entry.path != null) {
                Storage(
                    path = entry.path,
                    name = entry.name,
                    description = entry.description
                )
            } else {
                null
            }
        }
    }

    fun findStorage(path: String) = scope.async {
        val cachedStorage = storagesDao.getAll(path)
        if (cachedStorage?.path != null) {
            Storage(
                path = cachedStorage.path,
                name = cachedStorage.name,
                description = cachedStorage.description
            )
        } else {
            null
        }
    }

    fun deleteStorage(path: String) = scope.launch {
        File(path).deleteOnExit()
        storagesDao.delete(path)
    }

    fun addStorage(storage: Storage) = scope.launch {
        val cachedStorage = storagesDao.getAll(storage.path)
        var fileEntry = StorageFileEntry(
            path = storage.path,
            name = storage.name,
            description = storage.description
        )
        if (cachedStorage != null) fileEntry = fileEntry.copy(id = cachedStorage.id)
        storagesDao.insert(fileEntry)
    }


}