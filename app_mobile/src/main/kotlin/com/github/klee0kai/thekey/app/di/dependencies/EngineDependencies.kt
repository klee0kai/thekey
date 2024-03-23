package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.app.engine.findstorage.EditStorageEngine
import com.github.klee0kai.thekey.app.engine.findstorage.FindStorageEngine
import com.github.klee0kai.thekey.app.engine.storage.CryptStorage

interface EngineDependencies {

    fun findStorageEngineLazy(): AsyncCoroutineProvide<FindStorageEngine>

    fun editStorageEngineLazy(): AsyncCoroutineProvide<EditStorageEngine>

    fun cryptStorageEngineLazy(id: StorageIdentifier): AsyncCoroutineProvide<CryptStorage>

}