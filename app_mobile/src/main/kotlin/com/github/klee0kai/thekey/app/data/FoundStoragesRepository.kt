package com.github.klee0kai.thekey.app.data

import com.github.klee0kai.thekey.app.data.room.entry.toStorage
import com.github.klee0kai.thekey.app.data.room.entry.toStorageEntry
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.model.Storage
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class FoundStoragesRepository {

    private val storagesDao = DI.storageDaoLazy()
    private val scope = DI.ioThreadScope()

    val updateDbFlow = MutableSharedFlow<Unit>()

    fun getStorages() = scope.async {
        storagesDao().getAll().map { entry -> entry.toStorage() }
    }

    fun findStorage(path: String) = scope.async {
        storagesDao().get(path)?.toStorage()
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