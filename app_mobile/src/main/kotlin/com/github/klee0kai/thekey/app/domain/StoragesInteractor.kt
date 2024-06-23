package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StoragesInteractor {

    private val scope = DI.defaultThreadScope()
    private val rep = DI.storagesRepositoryLazy()
    private val engine = DI.findStorageEngineLazy()
    private val settings = DI.settingsRepositoryLazy()

    val allColorGroups = flow<List<ColorGroup>> {
        rep().allColorGroups.collect(this)
    }

    val allStorages = flow<List<ColoredStorage>> {
        rep().allStorages
            .map { list -> list.map { storage -> storage.updateVersion() } }
            .collect(this)
    }

    fun findStorage(path: String, mockNew: Boolean = false) = scope.async {
        var storage = rep().findStorage(path).await()?.updateVersion()
        if (storage == null && mockNew) {
            val newStorageVers = settings().newStorageVersion()
            storage = ColoredStorage(version = newStorageVers, path = path)
        }
        storage
    }

    fun setColorGroup(colorGroup: ColorGroup) = scope.launch { rep().setColorGroup(colorGroup).join() }

    fun deleteColorGroup(id: Long) = scope.launch { rep().deleteColorGroup(id).join() }

    private suspend fun ColoredStorage.updateVersion(): ColoredStorage {
        val version = engine().storageVersion(path = path)
        return copy(version = if (version > 0) version else settings().newStorageVersion())
    }
}