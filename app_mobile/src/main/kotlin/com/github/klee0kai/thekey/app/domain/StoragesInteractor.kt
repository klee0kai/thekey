package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.ColorGroup
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StoragesInteractor {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.storagesRepositoryLazy()
    private val engine = DI.engine().findStoragesEngine()
    private val settings = DI.settingsRepositoryLazy()

    val allColorGroups = flow<List<ColorGroup>> {
        rep().allColorGroups.collect(this)
    }
    val allStorages = flow<List<ColoredStorage>> {
        rep().allStorages
            .map { list -> list.map { storage -> storage.updateVersion() } }
            .collect(this)
    }

    fun findStorage(path: String) = scope.async { rep().findStorage(path).await()?.updateVersion() }

    fun setStorage(storage: ColoredStorage) = scope.launch { rep().setStorage(storage).join() }

    fun setStoragesGroup(storagePaths: List<String>, groupId: Long) = scope.launch { rep().setStoragesGroup(storagePaths, groupId).join() }

    fun deleteStorage(path: String) = scope.launch { rep().deleteStorage(path).join() }

    fun setColorGroup(colorGroup: ColorGroup) = scope.launch { rep().setColorGroup(colorGroup).join() }

    fun deleteColorGroup(id: Long) = scope.launch { rep().deleteColorGroup(id).join() }

    private suspend fun ColoredStorage.updateVersion(): ColoredStorage {
        val version = engine.storageVersion(path = path)
        return copy(version = if (version > 0) version else settings().newStorageVersion())
    }
}