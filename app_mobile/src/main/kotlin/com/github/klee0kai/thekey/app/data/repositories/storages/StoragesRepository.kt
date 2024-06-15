package com.github.klee0kai.thekey.app.data.repositories.storages

import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.utils.coroutine.completeAsync
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface StoragesRepository {

    val allColorGroups: Flow<List<ColorGroup>> get() = emptyFlow()
    val allStorages: Flow<List<ColoredStorage>> get() = emptyFlow()

    fun findStorage(path: String): Deferred<ColoredStorage?> = completeAsync(null)
    fun setStorage(storage: ColoredStorage): Job = emptyJob()
    fun setStoragesGroup(storagePaths: List<String>, groupId: Long): Job = emptyJob()
    fun deleteStorage(path: String): Job = emptyJob()


    fun setColorGroup(colorGroup: ColorGroup): Deferred<ColorGroup> = completeAsync(ColorGroup())
    fun deleteColorGroup(id: Long): Job = emptyJob()

}