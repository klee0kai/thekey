package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.engine.findstorage.EditStorageEngine
import com.github.klee0kai.thekey.app.engine.findstorage.FindStorageEngine
import com.github.klee0kai.thekey.app.engine.storage.CryptStorage
import com.github.klee0kai.thekey.app.engine.storage.CryptStorageSuspended
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide

interface EngineDependencies {

    fun findStorageEngineLazy(): AsyncCoroutineProvide<FindStorageEngine>

    fun editStorageEngineLazy(): AsyncCoroutineProvide<EditStorageEngine>

    fun cryptStorageEngineLazy(id: StorageIdentifier): AsyncCoroutineProvide<CryptStorage>

    fun cryptStorageEngineSafeLazy(id: StorageIdentifier): AsyncCoroutineProvide<CryptStorageSuspended>

}