package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.engine.editstorage.EditStorageEngine
import com.github.klee0kai.thekey.app.engine.editstorage.EditStorageSuspended
import com.github.klee0kai.thekey.app.engine.findstorage.FindStorageEngine
import com.github.klee0kai.thekey.app.engine.findstorage.FindStorageSuspended
import com.github.klee0kai.thekey.app.engine.storage.CryptStorage
import com.github.klee0kai.thekey.app.engine.storage.CryptStorageSuspended
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide

interface EngineDependencies {

    @Deprecated("use findStorageEngineSaveLazy")
    fun findStorageEngineLazy(): AsyncCoroutineProvide<FindStorageEngine>

    fun findStorageEngineSaveLazy(): AsyncCoroutineProvide<FindStorageSuspended>

    @Deprecated("use editStorageEngineSafeLazy")
    fun editStorageEngineLazy(): AsyncCoroutineProvide<EditStorageEngine>

    fun editStorageEngineSafeLazy(): AsyncCoroutineProvide<EditStorageSuspended>

    @Deprecated("use cryptStorageEngineSafeLazy")
    fun cryptStorageEngineLazy(id: StorageIdentifier): AsyncCoroutineProvide<CryptStorage>

    fun cryptStorageEngineSafeLazy(id: StorageIdentifier): AsyncCoroutineProvide<CryptStorageSuspended>

}